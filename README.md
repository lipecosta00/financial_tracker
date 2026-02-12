# Financial Tracker

Aplicacao Android modular para gerenciamento financeiro pessoal, com arquitetura limpa, persistencia local, sincronizacao com API simulada e dashboard de insights.

## Stack tecnologica

- Linguagem: Kotlin
- UI: XML (fluxo principal) + Jetpack Compose (dashboard)
- Arquitetura: MVVM + Clean Architecture
- DI: Koin
- Assincronia: Kotlin Coroutines + Flow
- Estado: StateFlow, SharedFlow e LiveData (interoperabilidade)
- Persistencia: Room (offline-first)
- Seguranca local: EncryptedSharedPreferences + Android Keystore
- Testes: JUnit, Truth, Espresso (Robot Pattern)

## Estrutura de modulos

- `:app`
  - Bootstrap da aplicacao, navegacao principal e montagem de DI.
- `:core`
  - Utilitarios compartilhados (money formatting, dispatchers, seguranca).
- `:domain`
  - Modelos de negocio, contratos de repositorio e casos de uso.
- `:data`
  - Room, API fake e implementacoes de repositorio.
- `:feature-auth`
  - Fluxo de autenticacao por PIN e biometria opcional.
- `:feature-transactions`
  - CRUD de transacoes, filtros e busca.
- `:feature-dashboard`
  - Dashboard Compose com resumo e analiticos.

## Arquitetura e fluxo de dados

### Camadas

1. `feature-*` (Presentation)
- Fragments/Compose + ViewModels.
- Consome casos de uso do `domain`.

2. `domain` (Business)
- Regras de negocio puras e independentes de Android framework.

3. `data` (Data)
- Implementa os contratos de repositorio.
- Usa Room como fonte principal e API fake para sincronizacao.

### Padrao de estado

- `StateFlow`: estado de tela (renderizacao continua).
- `SharedFlow`: eventos one-shot (mensagens e acoes pontuais).
- `LiveData`: ponte para interoperabilidade em pontos especificos.

## Persistencia e sincronizacao

- Banco local com Room (`FinancialDatabase`, `TransactionDao`).
- Repositorio de transacoes com comportamento offline-first.
- Operacoes de create/update/delete refletem no banco local e na API simulada.
- `refresh()` sincroniza dados remotos no armazenamento local sem perder consistencia funcional.

## Funcionalidades principais

### Autenticacao

- PIN local com hash armazenado de forma segura.
- Biometria opcional via AndroidX Biometric.

### Transacoes

- Criar, editar e excluir.
- Filtro por tipo (`All`, `Income`, `Expense`).
- Busca por descricao.
- Navegacao por mes (anterior/proximo/limpar).

### Dashboard

- Resumo mensal (income, expense, balance).
- Tendencia multi-mes.
- Breakdown de despesas por descricao.
- Top despesas do periodo.

## Qualidade e testes

### Unit tests

- Casos de uso de resumo e insights.
- ViewModels de autenticacao, transacoes e dashboard.

### UI tests

- Espresso com Robot Pattern para fluxos de transacoes.
- Robots encapsulam interacoes e assercoes para reduzir acoplamento dos testes.

## Requisitos de build

- `minSdk = 26`
- `targetSdk = 36`
- `compileSdk = 36`
- Java 11

## Comandos uteis

### Build

```powershell
.\gradlew assembleDebug
```

### Testes unitarios

```powershell
.\gradlew testDebugUnitTest
```

### Compilar testes instrumentados

```powershell
.\gradlew app:compileDebugAndroidTestKotlin
```

### Executar testes instrumentados (emulador/device)

```powershell
.\gradlew connectedDebugAndroidTest
```

## Notas operacionais

Em ambientes Windows, pode ocorrer lock temporario de `classes.jar` durante tarefas Gradle. Em caso de erro de lock:

```powershell
.\gradlew --stop
```

Em seguida, execute novamente o comando de build/teste.
