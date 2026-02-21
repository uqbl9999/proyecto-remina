# S1-T01: Estructura de Proyecto y Convenciones — Diseño Validado

Fecha: 2026-02-21
Sprint: 1 — Día 1
Ticket: S1-T01

## Decisiones de diseño

| Decisión | Elección | Razón |
|---|---|---|
| Estructura de repos | Monorepo | MVP 12 semanas, un solo Tech Lead haciendo merge |
| Layout del repo | Plano por tecnología | Sin overhead de workspaces JS por ahora |
| Build tool backend | Maven + Maven Wrapper | Predecible, agentes IA lo conocen bien |
| Mapeo entre clases | Mappers manuales | Mayor control sobre campos expuestos en DTOs |
| Lint Java | Checkstyle (falla build) | Tech Lead corrige estilo en code review |
| Lint JS | ESLint + Prettier | Estándar de facto para Next.js y Expo |
| Convención de ramas | `s{sprint}/t{ticket}-descripcion` | Trazabilidad directa al backlog |

## Estructura del repositorio

```
/
├── backend/
├── web/
├── mobile/
├── docs/
│   └── plans/
├── .github/
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── workflows/
├── .gitignore
└── README.md
```

## Backend (Spring Boot 3 + Java 21 + Maven)

```
backend/
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/com/colegioapp/
│   │   │   ├── iam/
│   │   │   ├── academic/
│   │   │   ├── finance/
│   │   │   ├── communications/
│   │   │   ├── integrations/
│   │   │   └── shared/
│   │   │       └── mapper/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
│       └── java/com/colegioapp/
├── checkstyle.xml
├── pom.xml
└── mvnw / mvnw.cmd
```

Dependencias iniciales: `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `flyway-core`, `postgresql`, `jjwt`, `lombok`. Sin MapStruct — mappers manuales por módulo.

## Web (Next.js 14+ + TypeScript)

```
web/
├── src/
│   ├── app/
│   │   ├── (auth)/
│   │   └── (dashboard)/
│   ├── components/
│   ├── lib/
│   └── types/
├── public/
├── .eslintrc.json
├── .prettierrc
├── tsconfig.json         (strict: true)
├── next.config.ts
└── package.json
```

## Mobile (Expo + TypeScript)

```
mobile/
├── app/
│   ├── (auth)/
│   └── (tabs)/
├── components/
├── lib/
├── .eslintrc.json
├── .prettierrc
├── tsconfig.json
├── app.json
└── package.json
```

## Convención de ramas

```
main                              # estable, solo merge via PR
s{sprint}/t{ticket}-descripcion

# Ejemplos
s1/t01-estructura-proyecto
s1/t03-auth-jwt
s2/t11-asistencia-aula
```

## PR Template (.github/PULL_REQUEST_TEMPLATE.md)

```markdown
## Ticket
S{sprint}-T{num}: [título]

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

## README raíz — comandos de arranque

```markdown
### Backend
cd backend && ./mvnw spring-boot:run

### Web
cd web && npm install && npm run dev

### Mobile
cd mobile && npm install && npx expo start
```

Prerequisitos: Java 21, Node 20+, Docker (PostgreSQL + Redis local).

## DoD

- [ ] Repositorio compila y levanta localmente (los 3 módulos)
- [ ] README técnico base con comandos de arranque
