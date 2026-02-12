# Financial Tracker

Aplicação Android modular para gerenciamento financeiro pessoal, com arquitetura limpa, persistência local, sincronização com API simulada e dashboard de insights.

## Stack tecnológica

- Linguagem: Kotlin
- UI: XML (fluxo principal) + Jetpack Compose (dashboard)
- Arquitetura: MVVM + Clean Architecture
- DI: Koin
- Assincronia: Kotlin Coroutines + Flow
- Estado: StateFlow, SharedFlow e LiveData (interoperabilidade)
- Persistência: Room (offline-first)
- Segurança local: EncryptedSharedPreferences + Android Keystore
- Testes: JUnit, Truth, Espresso (Robot Pattern)

## Estrutura de módulos

- `:app`
  - Bootstrap da aplicação, navegação principal e montagem de DI.
- `:core`
  - Utilitários compartilhados (money formatting, dispatchers, segurança).
- `:domain`
  - Modelos de negócio, contratos de repositório e casos de uso.
- `:data`
  - Room, API fake e implementações de repositório.
- `:feature-auth`
  - Fluxo de autenticação por PIN e biometria opcional.
- `:feature-transactions`
  - CRUD de transações, filtros e busca.
- `:feature-dashboard`
  - Dashboard Compose com resumo e analíticos.

## Arquitetura e fluxo de dados

### Camadas

1. `feature-*` (Presentation)
- Fragments/Compose + ViewModels.
- Consome casos de uso do `domain`.

2. `domain` (Business)
- Regras de negócio puras e independentes do framework Android.

3. `data` (Data)
- Implementa os contratos de repositório.
- Usa Room como fonte principal e API fake para sincronização.

### Padrão de estado

- `StateFlow`: estado de tela (renderização contínua).
- `SharedFlow`: eventos one-shot (mensagens e ações pontuais).
- `LiveData`: ponte para interoperabilidade em pontos específicos.

## Persistência e sincronização

- Banco local com Room (`FinancialDatabase`, `TransactionDao`).
- Repositório de transações com comportamento offline-first.
- Operações de create/update/delete refletem no banco local e na API simulada.
- `refresh()` sincroniza dados remotos no armazenamento local sem perder consistência funcional.

## Funcionalidades principais

### Autenticação

- PIN local com hash armazenado de forma segura.
- Biometria opcional via AndroidX Biometric.

### Transações

- Criar, editar e excluir.
- Filtro por tipo (`All`, `Income`, `Expense`).
- Busca por descrição.
- Navegação por mês (anterior/próximo/limpar).

### Dashboard

- Resumo mensal (income, expense, balance).
- Tendência multi-mês.
- Breakdown de despesas por descrição.
- Top despesas do período.

## Qualidade e testes

### Unit tests

- Casos de uso de resumo e insights.
- ViewModels de autenticação, transações e dashboard.

### UI tests

- Espresso com Robot Pattern para fluxos de transações.
- Robots encapsulam interações e asserções para reduzir acoplamento dos testes.

## Requisitos de build

- `minSdk = 26`
- `targetSdk = 36`
- `compileSdk = 36`
- Java 11

## Comandos úteis

### Build

```powershell
.\gradlew assembleDebug
```

### Testes unitários

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

Em ambientes Windows, pode ocorrer lock temporário de `classes.jar` durante tarefas Gradle. Em caso de erro de lock:

```powershell
.\gradlew --stop
```

Em seguida, execute novamente o comando de build/teste.
