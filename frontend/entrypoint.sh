#!/bin/sh
set -e

# Substitute only PORT and BACKEND_URL in the nginx template
# (envsubst with explicit var list leaves nginx $-variables intact)
envsubst '${PORT} ${BACKEND_URL}' < /tmp/nginx.conf.template > /etc/nginx/conf.d/default.conf

echo "nginx.conf generated — PORT=${PORT:-80} BACKEND_URL=${BACKEND_URL:-http://backend:8080}"

exec nginx -g 'daemon off;'
