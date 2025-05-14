#!/bin/bash

echo "[INFO] CI/CD 배포 시작: $(date)"

# .env 경로 명시
ENV_PATH="./springboot-docker/.env"

# 1. .env 파일 로딩
if [ -f "$ENV_PATH" ]; then
  export $(grep -v '^#' "$ENV_PATH" | xargs)
  echo "[INFO] .env 파일 로드 완료"
else
  echo "[ERROR] .env 파일이 존재하지 않습니다. [$ENV_PATH]"
  exit 1
fi

# 2. 설정 파일 복사
echo "[INFO] application.yml, application-secret.properties 복사 중..."
mkdir -p ./src/main/resources/
cp ./springboot-docker/secrets/application.yml ./src/main/resources/
cp ./springboot-docker/secrets/application-secret.properties ./src/main/resources/

# 3. 기존 컨테이너 종료
echo "[INFO] 기존 컨테이너 종료 중..."
docker compose --env-file "$ENV_PATH" down

# 4. 이미지 빌드
echo "[INFO] 이미지 재빌드 중..."
docker compose --env-file "$ENV_PATH" build --no-cache

# 5. 컨테이너 실행
echo "[INFO] 컨테이너 재기동 중..."
docker compose --env-file "$ENV_PATH" up -d

# 6. 기존 cloudflared 컨테이너 제거
if docker ps -a --format '{{.Names}}' | grep -q '^cloudflared$'; then
  echo "[INFO] 기존 cloudflared 컨테이너 제거"
  docker rm -f cloudflared
fi

# 7. cloudflared tunnel 실행
echo "[INFO] cloudflared tunnel 실행 중..."
docker run -d --name cloudflared cloudflare/cloudflared:latest \
  tunnel --no-autoupdate run --token "$CLOUDFLARED_TOKEN"

echo "[INFO] 배포 완료 ? : $(date)"
