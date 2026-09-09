# Build stage
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .
ARG VITE_API_BASE_URL=""
RUN npm run build

# Production stage
FROM nginx:alpine

# Bake the nginx template into the image. The official nginx entrypoint
# automatically runs envsubst over /etc/nginx/templates/*.template at startup
# (substituting ONLY defined env vars like ${PORT}/${BACKEND_URL}, leaving
# nginx's own $host/$uri/$scheme intact) and writes the result to
# /etc/nginx/conf.d/.
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
