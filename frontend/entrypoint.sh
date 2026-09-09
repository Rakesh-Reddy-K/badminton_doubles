#!/bin/sh

set -e

envsubst '${PORT} ${BACKEND_URL} ${BACKEND_HOST}' \
  < /tmp/nginx.conf.template \
  > /etc/nginx/conf.d/default.conf

exec nginx -g "daemon off;"
