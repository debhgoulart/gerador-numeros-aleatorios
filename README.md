# Gerador de Números Aleatórios

Aplicação Web em Java com JSF, JPA/Hibernate, arquitetura MVC e Servlet Filter.

---

## Especificações do Projeto

- **Java:** 21 (ou superior / compatível com Java 17+)
- **JSF:** 2.3 (Mojarra 2.3.19)
- **JPA / Hibernate:** JPA 2.2 / Hibernate Core 5.6.15.Final
- **Banco de Dados:** H2 Database (relacional embarcado, sem necessidade de instalação)
- **Build:** Apache Maven

---

## Como rodar o projeto?

### 1. Compilar e empacotar
Na raiz do projeto, execute:
```bash
mvn clean package
```

### 2. Iniciar o servidor
Execute o comando abaixo para subir o servidor local:
```bash
mvn jetty:run
```

### 3. Acessar a aplicação
Abra o navegador e acesse:
- **Página Inicial:** [http://localhost:8080/gerador/index.xhtml](http://localhost:8080/gerador/index.xhtml)
- **Histórico:** [http://localhost:8080/gerador/historico.xhtml](http://localhost:8080/gerador/historico.xhtml)

---

## Encerrar a aplicação
Para parar o servidor, pressione `Ctrl + C` no terminal onde o comando foi executado.
