#!/bin/bash
set -e

echo "[INFO] CI/CD 배포 시작: $(date)"

# 브랜치 이름
BRANCH_NAME=$BRANCH_NAME
echo "[INFO] 현재 브랜치: $BRANCH_NAME"

# ? 컨테이너 내부 기준 경로로 수정
ENV_PATH="/mnt/env/.env"
SOURCE_CODE_PATH="/mnt/env/app/source_code"
DEPLOY_PATH="/mnt/env/springboot-docker/deploy"  # ? Dockerfile 기준 경로로 수정
SECRETS_PATH="/mnt/env/secrets"
COMPOSE_FILE="/mnt/env/docker-compose.yml"

echo "[INFO] 설정된 DEPLOY_PATH: $DEPLOY_PATH"
echo "[INFO] 설정된 SOURCE_CODE_PATH: $SOURCE_CODE_PATH"
echo "[INFO] 설정된 COMPOSE_FILE: $COMPOSE_FILE"

# 1. .env 파일 로딩
if [ -f "$ENV_PATH" ]; then
  export $(grep -v '^#' "$ENV_PATH" | xargs)
  echo "[INFO] ? .env 파일 로드 완료"
else
  echo "[ERROR] ? .env 파일이 존재하지 않습니다. [$ENV_PATH]"
  exit 1
fi

# 2. develop 브랜치일 때만 빌드 수행
if [ "$BRANCH_NAME" = "develop" ]; then
  echo "[INFO] ? develop 브랜치 감지됨 - 빌드 및 복사 시작"

  echo "[STEP] ?? 최신 develop 코드 fetch 및 reset"
  cd "$WORKSPACE" || { echo "[ERROR] ? WORKSPACE 진입 실패"; exit 1; }
  git fetch origin develop
  git reset --hard origin/develop
  echo "[INFO] ? 최신 develop 코드로 리셋 완료"

  echo "[STEP] ?? source_code 디렉터리 초기화 및 복사"
  rm -rf "$SOURCE_CODE_PATH"
  cp -r "$WORKSPACE" "$SOURCE_CODE_PATH"
  echo "[INFO] ? source_code 복사 완료"

  echo "[STEP] ?? 설정 파일 복사"
  cp "$SECRETS_PATH/application.yml" "$SOURCE_CODE_PATH/eroom/src/main/resources/"
  cp "$SECRETS_PATH/application-secret.properties" "$SOURCE_CODE_PATH/eroom/src/main/resources/"
  echo "[INFO] ? 설정 파일 덮어쓰기 완료"

  echo "[STEP] ??? .jar 빌드 시작"
  cd "$SOURCE_CODE_PATH/eroom" || { echo "[ERROR] ? eroom 폴더 진입 실패"; exit 1; }
  ./gradlew clean bootJar
  echo "[INFO] ? gradle build 완료"
  echo "[DEBUG] ?? 빌드된 jar 파일 목록:"
  ls -lh build/libs

  echo "[STEP] ?? deploy 디렉터리 정리"
  mkdir -p "$DEPLOY_PATH"
  rm -f "$DEPLOY_PATH"/*.jar
  echo "[DEBUG] ?? deploy 디렉터리 상태:"
  ls -lh "$DEPLOY_PATH"

  echo "[STEP] ?? .jar 복사 → deploy 디렉터리 (이름 고정: app.jar)"
  cp build/libs/eroom-0.0.1-SNAPSHOT.jar "$DEPLOY_PATH/app.jar"

  if [ ! -f "$DEPLOY_PATH/app.jar" ]; then
    echo "[ERROR] ? JAR 복사 실패! app.jar 없음."
    exit 1
  fi

  echo "[INFO] ? JAR 복사 성공: $(ls -lh $DEPLOY_PATH/app.jar)"
else
  echo "[INFO] ?? 현재 브랜치는 $BRANCH_NAME. 빌드 스킵됨."
fi

# 3. 기존 컨테이너 종료
echo "[STEP] ?? 기존 컨테이너 종료 중..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_PATH" down
echo "[INFO] ? 컨테이너 종료 완료"

# 4. Docker 이미지 재빌드
echo "[STEP] ?? Docker 이미지 재빌드 중 (--no-cache)..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_PATH" build --no-cache
echo "[INFO] ? 이미지 재빌드 완료"

# 5. 컨테이너 재기동
echo "[STEP] ?? 컨테이너 재기동 중..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_PATH" up -d
echo "[INFO] ? 컨테이너 기동 완료"

# 6. 기존 cloudflared 제거
if docker ps -a --format '{{.Names}}' | grep -q '^cloudflared$'; then
  echo "[STEP] ??? cloudflared 컨테이너 제거"
  docker rm -f cloudflared
  echo "[INFO] ? cloudflared 제거 완료"
fi

# 7. cloudflared tunnel 재시작
echo "[STEP] ?? cloudflared tunnel 실행"
docker run -d --name cloudflared \
  -v ~/.cloudflared:/etc/cloudflared \
  cloudflare/cloudflared:latest \
  tunnel --config /etc/cloudflared/config.yml run
echo "[INFO] ? cloudflared tunnel 기동 완료"
