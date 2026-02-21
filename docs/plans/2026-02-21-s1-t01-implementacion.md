# S1-T01: Estructura de Proyecto y Convenciones — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Dejar el monorepo con los tres módulos (backend, web, mobile) compilando y levantando localmente, con convenciones de ramas, PR template, lint y README técnico.

**Architecture:** Monorepo plano con carpetas `backend/` (Spring Boot 3 + Java 21 + Maven), `web/` (Next.js 14 + TypeScript) y `mobile/` (Expo + TypeScript). Mappers manuales en backend, Checkstyle para Java, ESLint + Prettier para JS/TS.

**Tech Stack:** Java 21, Spring Boot 3.4.x, Maven, Next.js 14, Expo SDK 52, TypeScript, Checkstyle, ESLint, Prettier.

**Rama de trabajo:** `s1/t01-estructura-proyecto`

---

### Task 1: Crear rama de trabajo y scaffolding raíz

**Files:**
- Create: `.gitignore`
- Create: `README.md`
- Create: `.github/PULL_REQUEST_TEMPLATE.md`

**Step 1: Crear y checkout la rama del ticket**

```bash
git checkout -b s1/t01-estructura-proyecto
```

Expected: rama creada y activa.

**Step 2: Crear `.gitignore` raíz**

```
# Java / Maven
backend/target/
backend/.mvn/wrapper/maven-wrapper.jar
*.class
*.jar
*.war

# Node
node_modules/
web/.next/
web/out/
mobile/.expo/
mobile/dist/

# Env
.env
.env.local
.env.*.local

# IDE
.idea/
.vscode/
*.iml
.DS_Store
```

**Step 3: Crear `.github/PULL_REQUEST_TEMPLATE.md`**

```markdown
## Ticket
S{sprint}-T{num}: [título del ticket]

## Qué hace este PR
-

## DoD checklist
- [ ] Compila y tests pasan
- [ ] Manejo de errores cubierto
- [ ] Logs / auditoría donde aplica
- [ ] README actualizado si hay nuevo comando

## Cómo probar localmente
1.
```

**Step 4: Crear `README.md` esqueleto**

```markdown
# ColegioApp — Monorepo

SaaS para colegios privados peruanos (200-800 alumnos).

## Prerequisitos

- Java 21+
- Node 20+
- Docker (para PostgreSQL y Redis local)

## Módulos

| Módulo | Tecnología | Puerto local |
|--------|-----------|--------------|
| backend | Spring Boot 3 + Java 21 | 8080 |
| web | Next.js 14 | 3000 |
| mobile | Expo | 8081 |

## Arranque local

### Backend
\`\`\`bash
cd backend
./mvnw spring-boot:run
\`\`\`

### Web
\`\`\`bash
cd web
npm install
npm run dev
\`\`\`

### Mobile
\`\`\`bash
cd mobile
npm install
npx expo start
\`\`\`

## Convención de ramas

\`\`\`
s{sprint}/t{ticket}-descripcion-corta

# Ejemplos
s1/t01-estructura-proyecto
s1/t03-auth-jwt
s2/t11-asistencia-aula
\`\`\`

## Variables de entorno

Cada módulo tiene su propio `.env.example`. Copiar y renombrar a `.env` antes de arrancar.

- `backend/.env.example`
- `web/.env.example`
- `mobile/.env.example`
```

**Step 5: Commit**

```bash
git add .gitignore README.md .github/PULL_REQUEST_TEMPLATE.md
git commit -m "s1/t01: scaffolding raiz, gitignore, README y PR template"
```

---

### Task 2: Scaffold del backend (Spring Boot 3 + Java 21 + Maven)

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/checkstyle.xml`
- Create: `backend/src/main/java/com/colegioapp/ColegioAppApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/.env.example`

**Step 1: Generar proyecto base con Spring Initializr**

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.4.2 \
  -d baseDir=backend \
  -d groupId=com.colegioapp \
  -d artifactId=colegioapp-backend \
  -d name=colegioapp-backend \
  -d packageName=com.colegioapp \
  -d javaVersion=21 \
  -d dependencies=web,security,data-jpa,flyway,postgresql,lombok \
  -o backend.zip && unzip backend.zip && rm backend.zip
```

Expected: carpeta `backend/` creada con `pom.xml`, `mvnw`, `src/`.

**Step 2: Agregar dependencias faltantes al `pom.xml`**

Agregar dentro de `<dependencies>` (jjwt para JWT en S1-T03):

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

**Step 3: Agregar plugin Checkstyle al `pom.xml`**

Agregar dentro de `<build><plugins>`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.4.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <failsOnError>true</failsOnError>
        <consoleOutput>true</consoleOutput>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Step 4: Crear `backend/checkstyle.xml`**

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="severity" value="error"/>
    <module name="TreeWalker">
        <!-- Imports -->
        <module name="UnusedImports"/>
        <module name="AvoidStarImport"/>
        <!-- Naming -->
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="ParameterName"/>
        <!-- Bloques -->
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
        <!-- Longitud -->
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <!-- Espacios -->
        <module name="WhitespaceAround"/>
        <module name="NoWhitespaceAfter"/>
    </module>
    <!-- Tabs vs spaces -->
    <module name="FileTabCharacter"/>
</module>
```

**Step 5: Crear estructura de paquetes**

```bash
mkdir -p backend/src/main/java/com/colegioapp/{iam,academic,finance,communications,integrations,shared/mapper}
mkdir -p backend/src/main/resources/db/migration
mkdir -p backend/src/test/java/com/colegioapp
```

**Step 6: Reemplazar `application.yml` con configuración base**

```yaml
spring:
  application:
    name: colegioapp-backend
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/colegioapp}
    username: ${DB_USER:colegioapp}
    password: ${DB_PASSWORD:colegioapp}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080

logging:
  level:
    com.colegioapp: INFO
```

**Step 7: Crear `backend/.env.example`**

```
DB_URL=jdbc:postgresql://localhost:5432/colegioapp
DB_USER=colegioapp
DB_PASSWORD=colegioapp
JWT_SECRET=cambia-esto-por-un-secreto-de-256-bits
```

**Step 8: Verificar que el backend compila**

```bash
cd backend && ./mvnw compile -q
```

Expected: `BUILD SUCCESS` sin errores de compilación ni Checkstyle.

Si falla Checkstyle en el código generado por Initializr: ajustar el archivo infractor (usualmente el archivo de tests) para cumplir las reglas.

**Step 9: Commit**

```bash
git add backend/
git commit -m "s1/t01: scaffold backend Spring Boot 3, Maven, Checkstyle, estructura de paquetes"
```

---

### Task 3: Scaffold del frontend web (Next.js 14 + TypeScript)

**Files:**
- Create: `web/` (via create-next-app)
- Create: `web/.prettierrc`
- Create: `web/.env.example`

**Step 1: Generar proyecto Next.js**

```bash
npx create-next-app@14 web \
  --typescript \
  --eslint \
  --app \
  --src-dir \
  --no-tailwind \
  --import-alias "@/*" \
  --no-git
```

Expected: carpeta `web/` generada con App Router, TypeScript y ESLint configurados.

**Step 2: Instalar Prettier y configurarlo**

```bash
cd web && npm install --save-dev prettier eslint-config-prettier
```

**Step 3: Crear `web/.prettierrc`**

```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "all",
  "printWidth": 100
}
```

**Step 4: Actualizar `web/.eslintrc.json` para incluir Prettier**

```json
{
  "extends": ["next/core-web-vitals", "prettier"]
}
```

**Step 5: Habilitar strict mode en `web/tsconfig.json`**

Verificar que exista `"strict": true` en `compilerOptions`. Si no está, agregarlo.

**Step 6: Crear estructura de carpetas adicionales**

```bash
mkdir -p web/src/{components,lib,types}
```

**Step 7: Crear `web/.env.example`**

```
NEXT_PUBLIC_API_URL=http://localhost:8080
```

**Step 8: Verificar que el web compila**

```bash
cd web && npm run build
```

Expected: build exitoso sin errores TypeScript.

**Step 9: Commit**

```bash
git add web/
git commit -m "s1/t01: scaffold web Next.js 14, TypeScript strict, ESLint, Prettier"
```

---

### Task 4: Scaffold del mobile (Expo + TypeScript)

**Files:**
- Create: `mobile/` (via create-expo-app)
- Create: `mobile/.eslintrc.json`
- Create: `mobile/.prettierrc`
- Create: `mobile/.env.example`

**Step 1: Generar proyecto Expo**

```bash
npx create-expo-app@latest mobile --template blank-typescript
```

Expected: carpeta `mobile/` con Expo Router y TypeScript.

**Step 2: Instalar ESLint + Prettier**

```bash
cd mobile && npx expo install eslint eslint-config-expo prettier eslint-config-prettier --dev
```

**Step 3: Crear `mobile/.eslintrc.json`**

```json
{
  "extends": ["expo", "prettier"],
  "rules": {
    "no-unused-vars": "error"
  }
}
```

**Step 4: Crear `mobile/.prettierrc`**

```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "all",
  "printWidth": 100
}
```

**Step 5: Crear estructura de carpetas**

```bash
mkdir -p mobile/{components,lib}
```

**Step 6: Crear `mobile/.env.example`**

```
EXPO_PUBLIC_API_URL=http://localhost:8080
```

**Step 7: Verificar que el mobile compila**

```bash
cd mobile && npx expo export --platform web 2>&1 | tail -5
```

Expected: sin errores TypeScript. (Compilación web de Expo como validación rápida sin dispositivo.)

**Step 8: Commit**

```bash
git add mobile/
git commit -m "s1/t01: scaffold mobile Expo, TypeScript, ESLint, Prettier"
```

---

### Task 5: Verificación final de DoD y cierre de rama

**Step 1: Verificar backend levanta**

Requiere PostgreSQL corriendo en localhost:5432. Si no hay DB local aún (se configura en S1-T02), verificar solo que compila:

```bash
cd backend && ./mvnw compile -q && echo "OK: backend compila"
```

Expected: `OK: backend compila`

**Step 2: Verificar web levanta**

```bash
cd web && npm run dev &
sleep 5 && curl -s -o /dev/null -w "%{http_code}" http://localhost:3000
```

Expected: `200`

**Step 3: Verificar lint pasa en web**

```bash
cd web && npm run lint
```

Expected: sin errores.

**Step 4: Verificar lint pasa en mobile**

```bash
cd mobile && npx eslint . --ext .ts,.tsx
```

Expected: sin errores.

**Step 5: Commit final del README completo (si se hicieron ajustes)**

```bash
git add README.md
git commit -m "s1/t01: README tecnico completo con comandos de arranque"
```

**Step 6: Abrir PR hacia main**

```bash
gh pr create \
  --title "S1-T01: Estructura de proyecto y convenciones" \
  --body "## Ticket
S1-T01: Estructura de proyecto y convenciones

## Qué hace este PR
- Monorepo con backend (Spring Boot 3, Java 21, Maven), web (Next.js 14) y mobile (Expo)
- Checkstyle para Java, ESLint + Prettier para JS/TS
- Convención de ramas y PR template
- README técnico con comandos de arranque

## DoD checklist
- [ ] Compila y tests pasan
- [ ] README actualizado con comandos de arranque
- [ ] Checkstyle configurado y pasando en backend
- [ ] ESLint + Prettier configurados en web y mobile

## Cómo probar localmente
1. \`cd backend && ./mvnw compile\`
2. \`cd web && npm run build\`
3. \`cd mobile && npx eslint . --ext .ts,.tsx\`"
```

---

## Notas para el agente ejecutor

- **No instalar MapStruct** — los mappers serán clases manuales en `shared/mapper/` y en cada módulo.
- **Checkstyle falla el build** — si el código generado por Initializr tiene violaciones, corregirlas antes del commit (no deshabilitar el plugin).
- **Sin DB en S1-T01** — el backend no levanta completamente sin PostgreSQL. La verificación de compilación (`mvnw compile`) es suficiente para el DoD de esta tarea; el levantamiento completo se valida en S1-T02.
- **Expo export --platform web** es solo para validar TypeScript sin necesitar un dispositivo/simulador.
