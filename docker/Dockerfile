# Estágio de build
FROM openjdk:11.0.4-jre-slim-buster

WORKDIR /app

# Clonar o projeto do GitHub
RUN git clone <https://github.com/francescokrum/Teatro> .

# Instalar dependências
RUN npm install

# Compilar o projeto
RUN npm run build

# Estágio de produção
FROM openjdk

WORKDIR /app

# Copiar os arquivos compilados do estágio de build
COPY --from=builder /app .

# Instalar módulos necessários para execução
RUN npm install --only=production

# Expor a porta 8080
EXPOSE 8080

# Definir o comando para inicialização do projeto
ENTRYPOINT ["npm", "start"]
