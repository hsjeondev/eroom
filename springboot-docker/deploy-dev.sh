#!/bin/bash
set -e  # 에러 발생 시 스크립트 중단

echo "[INFO] 🚀 CI/CD 배포 시작: $(date)"

# 브랜치 이름 (Jenkins 멀티브랜치 파이프라인에서 제공됨)
BRANCH_NAME=$BRANCH_NAME

# 절대 경로 설정
ENV_PATH="/mnt/env/.env"
SOURCE_CODE_PATH="/mnt/env/app/source_code"
DEPLOY_PATH="/mnt/env/app/deploy"
SECRETS_PATH="/mnt/env/secrets"

# 1. .env 파일 로딩
if [ -f "$ENV_PATH" ]; then
  export $(grep -v '^#' "$ENV_PATH" | xargs)
  echo "[INFO] ✅ .env 파일 로드 완료"
else
  echo "[ERROR] ❌ .env 파일이 존재하지 않습니다. [$ENV_PATH]"
  exit 1
fi

# 2. develop 브랜치에 한해 소스 복사 및 jar 빌드
if [ "$BRANCH_NAME" = "develop" ]; then
  echo "[INFO] 🔍 develop 브랜치 감지됨 - 소스 복사 및 JAR 빌드 수행"

  echo "[STEP] 📦 최신 develop 코드 강제 fetch"
  cd "$WORKSPACE"
  git fetch origin develop
  git reset --hard origin/develop
  echo "[DEBUG] 현재 커밋: $(git log -1 --pretty=oneline)"
  echo "[INFO] ✅ 최신 코드로 리셋 완료"

  echo "[STEP] 🧹 기존 소스 제거 후 복사"
  rm -rf "$SOURCE_CODE_PATH"
  cp -r "$WORKSPACE" "$SOURCE_CODE_PATH"

  echo "[STEP] 🧾 설정 파일 덮어쓰기"
  cp "$SECRETS_PATH/application.yml" "$SOURCE_CODE_PATH/eroom/src/main/resources/"
  cp "$SECRETS_PATH/application-secret.properties" "$SOURCE_CODE_PATH/eroom/src/main/resources/"

  echo "[STEP] ⚙️ .jar 빌드 시작"
  cd "$SOURCE_CODE_PATH/eroom"
  ./gradlew clean bootJar

  echo "[STEP] 📤 JAR 복사 → deploy 디렉터리"
  mkdir -p "$DEPLOY_PATH"
  echo "[DEBUG] build/libs 내용:"
  ls -lh build/libs || { echo "[ERROR] ❌ JAR 빌드 실패 또는 경로 없음"; exit 1; }

  rm -f "$DEPLOY_PATH"/*.jar
  cp build/libs/*.jar "$DEPLOY_PATH/"
  echo "[INFO] ✅ JAR 복사 완료: $(ls $DEPLOY_PATH/*.jar)"
else
  echo "[INFO] ℹ️ $BRANCH_NAME 브랜치는 배포 대상이 아님. 스킵."
fi

# 3. 기존 컨테이너 종료
echo "[STEP] 🛑 기존 컨테이너 종료 중..."
docker compose -f /mnt/env/docker-compose.yml --env-file "$ENV_PATH" down

# 4. 이미지 재빌드
echo "[STEP] 🔁 Docker 이미지 재빌드 중 (--no-cache)..."
docker compose -f /mnt/env/docker-compose.yml --env-file "$ENV_PATH" build --no-cache

# 5. 컨테이너 재기동
echo "[STEP] 🚀 컨테이너 재기동 중..."
docker compose -f /mnt/env/docker-compose.yml --env-file "$ENV_PATH" up -d

# 6. 기존 cloudflared 컨테이너 제거
if docker ps -a --format '{{.Names}}' | grep -q '^cloudflared$'; then
  echo "[INFO] 🧹 기존 cloudflared 컨테이너 제거"
  docker rm -f cloudflared
fi

# 7. cloudflared tunnel 실행 (config.yml 기반)
echo "[STEP] 🌐 cloudflared tunnel 실행 중..."
docker run -d --name cloudflared \
  -v ~/.cloudflared:/etc/cloudflared \
  cloudflare/cloudflared:latest \
  tunnel --config /etc/cloudflared/config.yml run

echo "[SUCCESS] ✅ CI/CD 배포 완료: $(date)"
