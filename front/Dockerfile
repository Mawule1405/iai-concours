# Stage 1: Build
# Étape 1 : Build avec Node
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./

# On ajoute le flag --legacy-peer-deps pour contourner l'erreur ERESOLVE
RUN npm install --legacy-peer-deps

COPY . .
RUN npm run build --configuration=production

# Étape 2 : Runtime avec Nginx
FROM nginx:alpine
# Assure-toi que le chemin 'dist/front/browser' correspond bien à ton projet
COPY --from=build /app/dist/front/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

