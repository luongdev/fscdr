FROM node:16-alpine as base
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
ENV TZ Asia/Ho_Chi_Minh

ENV BASE_API_URL "http://localhost"

COPY --from=base /usr/src/app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
