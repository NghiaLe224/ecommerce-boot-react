# 🐳 Dockerize sb-ecom (Spring Boot + React + MySQL)

This bundle includes production-ready Dockerfiles and a `docker-compose.yml` for your project structure:

```
sb-ecom/
  backend/   -> Spring Boot (Maven, Java 21)
  frontend/  -> React (Vite)
```

## 1) Files included

- `docker-compose.yml` – Orchestrates MySQL, backend, and frontend
- `.env.example` – Template for your local `.env` (put real secrets in `.env`, never commit them)
- `sb-ecom/backend/Dockerfile` – Multi-stage build for Spring Boot (Maven build → Temurin JRE run)
- `sb-ecom/backend/.dockerignore` – Speeds up build, avoids context bloat
- `sb-ecom/frontend/Dockerfile` – Multi-stage build (Node build → Nginx serve)
- `sb-ecom/frontend/.dockerignore`
- `sb-ecom/frontend/docker/nginx/default.conf` – Nginx that serves SPA and proxies `/api` and `/public` → backend

> Note: The compose file exposes ports 3306 (MySQL), 8080 (backend), and 80 (frontend).

## 2) Prepare your environment

1. Copy everything from this bundle into the **root** of your repository (alongside the `sb-ecom` folder).
2. Create a local `.env` by copying the template:
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and change **all** placeholder values (passwords, secrets).

3. Ensure your Spring Boot app does **not** hardcode `localhost` in `spring.datasource.url`. In container, use host `mysql`:
   ```properties
   spring.datasource.url=jdbc:mysql://mysql:3306/ecom?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   ```
   You can override via environment variables already wired in `docker-compose.yml`:
   `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

4. If your API is not under `/api` or `/public`, update `frontend/docker/nginx/default.conf` accordingly.

## 3) Build & run (Windows 11 + Docker Desktop + WSL2)

From the project root (where `docker-compose.yml` lives):
```bash
docker compose up -d --build
```

Check containers:
```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f mysql
```

Open:
- Frontend (React via Nginx): http://localhost/
- Backend (Spring Boot):      http://localhost:8080/
- Example public endpoint:    http://localhost/public/products

## 4) Using existing MySQL data volume

The compose file uses a **named volume** `mysql_data`. If you already have a local named volume with your data, Docker will reuse it automatically when the name matches. To list volumes:
```bash
docker volume ls
```

If your existing volume name differs, edit the `volumes:` section at the bottom of `docker-compose.yml` and set `name: your_existing_volume`.

## 5) Common tweaks

- **CORS:** When you serve the React app and proxy API via the same Nginx, the browser uses the **same origin** → CORS is typically not needed. If you're hitting CORS errors, either keep the proxy paths or enable CORS in Spring.
- **DB init scripts:** Put `*.sql` files in `./mysql/init/` and uncomment that mount to initialize on first run.
- **Memory/Java:** Pass custom JVM flags via `JAVA_OPTS`, e.g.:
  ```yaml
  environment:
    JAVA_OPTS: "-Xms256m -Xmx512m"
  ```
- **Healthchecks:** You can enable Spring Boot Actuator and uncomment the `HEALTHCHECK` in the backend Dockerfile.

## 6) Dev vs Prod

- **Prod (recommended):** Use this setup. Backend runs jar, frontend is static under Nginx.
- **Dev:** You might prefer live-reload. Typical pattern is a `docker-compose.dev.yml` with bind mounts for code and separate `npm run dev`:
  - Backend: mount `backend` and run `mvn spring-boot:run`
  - Frontend: mount `frontend` and run `npm run dev` on port 5173, set `VITE_API_URL=http://localhost:8080`

## 7) Secrets & GitHub push protection

Never commit real secrets. Use `.env` locally and CI/CD secret stores in production (e.g., GitHub Actions Secrets, AWS SSM). If push protection blocks you, scan your repo history and rotate any exposed credentials.

## 8) Useful commands

```bash
# Rebuild only a service
docker compose build backend
docker compose up -d backend

# Connect to MySQL
docker exec -it sb-ecom-mysql mysql -u root -p

# Tail logs
docker compose logs -f frontend
docker compose logs -f backend
```

---

If anything differs in your codebase (paths, ports, API prefixes), update the few commented lines. Happy shipping! 🚀
