# Folio Docker 환경

## PostgreSQL

### 실행

```bash
docker compose -f docker-postgres-compose.yml up -d
```

### 종료 (컨테이너만 중지, 데이터 유지)

```bash
docker compose -f docker-postgres-compose.yml down
```

### 완전 삭제 (볼륨 포함, 데이터 전부 삭제)

```bash
docker compose -f docker-postgres-compose.yml down -v
```

### 로그 확인

```bash
docker compose -f docker-postgres-compose.yml logs -f
```

### 컨테이너 상태 확인

```bash
docker compose -f docker-postgres-compose.yml ps
```

### psql 접속

```bash
docker exec -it folio-postgres psql -U folio -d folio
```

### 재시작

```bash
docker compose -f docker-postgres-compose.yml restart
```

### 재빌드 (compose 파일 수정 후)

```bash
docker compose -f docker-postgres-compose.yml up -d --force-recreate
```

---

> **참고:** `init/` 폴더의 SQL은 최초 볼륨 생성 시에만 실행됩니다.
> init SQL을 다시 적용하려면 볼륨을 삭제(`down -v`)한 후 재실행해야 합니다.
