FROM node:18-alpine

WORKDIR /app

COPY package.json ./
COPY package-lock.json ./
RUN npm install

COPY server.js ./

EXPOSE 8081

CMD ["npm", "start"]
