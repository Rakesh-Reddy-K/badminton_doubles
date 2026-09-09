#!/bin/sh

set -e

# Fallback defaults if env vars are not set on Render
BACKEND_URL="${BACKEND_URL:-https://badminton-backend-9a2r.onrender.com}"
BACKEND_HOST="${BACKEND_HOST:-badminton-backend-9a2r.onrender.com}"
PORT="${PORT:-10000}"

echo "=== nginx entrypoint ==="
echo "PORT=$PORT"
echo "BACKEND_URL=$BACKEND_URL"
echo "BACKEND_HOST=$BACKEND_HOST"
echo "Template exists: $(test -f /etc/nginx/nginx.conf.template && echo YES || echo NO)"

envsubst '${PORT} ${BACKEND_URL} ${BACKEND_HOST}' \
  < /etc/nginx/nginx.conf.template \
  > /etc/nginx/conf.d/default.conf

echo "=== Generated config ==="
cat /etc/nginx/conf.d/default.conf
echo "=== End config ==="

exec nginx -g "daemon off;"
