# Financial Tracker - Documentacao Tecnica

Documentacao tecnica detalhada do projeto de estudo Android com foco em boas praticas para cenarios financeiros.

## 1. Objetivo tecnico

Praticar, em um app pequeno, um conjunto de competencias de nivel profissional:

- Kotlin e Android moderno
- XML legado + Compose incremental
- MVVM + Clean Architecture
- Injeção de dependencia com Koin
- Concorrencia com Coroutines
- Estado com LiveData, StateFlow e SharedFlow
- Modularizacao
- Testes unitarios e UI (Robot Pattern)
- Segurança de dados locais

## 2. Arquitetura

### 2.1 Camadas

- `domain`: regras de negocio puras (sem Android framework)
- `data`: implementacoes de repositorios e fontes de dados
- `feature-*`: apresentacao por feature (UI + ViewModel)
- `core`: componentes compartilhados transversais
- `app`: bootstrap, DI root e navegacao principal

### 2.2 Padrao de apresentacao (MVVM)

- `ViewModel` recebe use cases por DI.
- UI observa estado e eventos.
- Estado principal em `StateFlow`.
- Eventos one-shot em `SharedFlow`.
- `LiveData` exposta para compatibilidade/estudo de legado.

Arquivo chave: `feature-transactions/src/main/java/com/example/feature/transactions/ui/TransactionsViewModel.kt`

## 3. Modularizacao

### 3.1 Modulos

- `:app`
- `:core`
- `:domain`
- `:data`
- `:feature-transactions`
- `:feature-dashboard`

Configuracao: `settings.gradle.kts`

### 3.2 Motivacao

- Reduz acoplamento.
- Facilita testes isolados.
- Permite evolucao por feature.
- Aproxima o projeto de estruturas reais de producao.

## 4. Tecnologias e decisoes

### Kotlin

- Linguagem principal.
- Escolha por seguranca de tipos, concisao e ecossistema Android atual.

### XML + Fragment (fluxo principal)

- Tela principal de transacoes em XML.
- Objetivo: reforcar dominio de UI classica ainda exigida no mercado.

Arquivos:
- `feature-transactions/src/main/res/layout/fragment_transactions.xml`
- `feature-transactions/src/main/java/com/example/feature/transactions/ui/TransactionsFragment.kt`

### Jetpack Compose (uso obrigatorio minimo)

- Dashboard implementado com `ComposeView` dentro de `Fragment`.
- Estrategia de adocao incremental sem reescrever toda a UI.

Arquivo:
- `feature-dashboard/src/main/java/com/example/feature/dashboard/ui/DashboardFragment.kt`

### Koin

- DI por modulos simples.
- Registro de dependencias em `app/di`.

Arquivos:
- `app/src/main/java/com/example/financialtraker/di/AppModule.kt`
- `app/src/main/java/com/example/financialtraker/di/DataModule.kt`
- `app/src/main/java/com/example/financialtraker/di/DomainModule.kt`
- `app/src/main/java/com/example/financialtraker/di/FeatureModule.kt`

### Coroutines + Flow

- Concorrencia estruturada e observacao reativa de dados.
- Melhor controle de thread e cancelamento.

### LiveData + StateFlow + SharedFlow

- `StateFlow`: estado de tela observavel.
- `SharedFlow`: eventos efemeros (toast/mensagem).
- `LiveData`: interoperabilidade com legado e ciclo de vida Android tradicional.

### Seguranca local

- `EncryptedSharedPreferences` com `MasterKey` (Keystore).
- Aplicado em `SecureTokenStore`.

Arquivo:
- `core/src/main/java/com/example/core/security/SecureTokenStore.kt`

### Precisao monetaria

- Uso de `BigDecimal` no dominio financeiro.
- Normalizacao centralizada em `MoneyFormatter`.

Arquivos:
- `domain/src/main/java/com/example/domain/model/FinancialTransaction.kt`
- `core/src/main/java/com/example/core/money/MoneyFormatter.kt`

## 5. Fluxo funcional atual

1. `MainActivity` sobe com tema Material Components.
2. Carrega `TransactionsFragment` por padrao.
3. Usuario visualiza lista e resumo mensal.
4. Usuario adiciona transacao por dialog.
5. ViewModel aciona use case e repository.
6. Fake API atualiza dados e fluxo reemite estado.
7. Usuario pode navegar para dashboard Compose.

Arquivos:
- `app/src/main/java/com/example/financialtraker/MainActivity.kt`
- `app/src/main/res/layout/activity_main.xml`

## 6. Dados e fake backend

### 6.1 Abstracao de repositorio

Contrato no dominio:
- `domain/src/main/java/com/example/domain/repository/TransactionRepository.kt`

Implementacao no data:
- `data/src/main/java/com/example/data/repository/TransactionRepositoryImpl.kt`

### 6.2 Fake API

- `FakeTransactionApi` simula latencia e resposta de servidor.
- Objetivo: estudo e previsibilidade sem backend real.

Arquivo:
- `data/src/main/java/com/example/data/source/TransactionApi.kt`

## 7. Testes

### 7.1 Unitarios

- Regra de resumo mensal:
  - `domain/src/test/java/com/example/domain/usecase/CalculateMonthlySummaryUseCaseTest.kt`
- Comportamento do ViewModel:
  - `feature-transactions/src/test/java/com/example/feature/transactions/ui/TransactionsViewModelTest.kt`

### 7.2 UI (Robot Pattern)

- Robot:
  - `app/src/androidTest/java/com/example/financialtraker/robot/TransactionsRobot.kt`
- Teste:
  - `app/src/androidTest/java/com/example/financialtraker/MainActivityRobotTest.kt`

## 8. Build e execucao

### Build

```powershell
.\gradlew assembleDebug
```

### Testes unitarios

```powershell
.\gradlew testDebugUnitTest
```

### Testes instrumentados (com emulador/device)

```powershell
.\gradlew connectedDebugAndroidTest
```

## 9. Decisoes de engenharia relevantes

- Escolha de XML no fluxo principal para cobrir requisito de experiencia avancada em layouts classicos.
- Compose mantido em uma feature para adocao progressiva.
- `AppCompatActivity` com tema Material Components para compatibilidade e estabilidade.
- `BigDecimal` e normalizacao monetaria para minimizar risco de erros financeiros.
- Estrutura preparada para evoluir para Room, Retrofit e autenticação robusta.

## 10. Roadmap tecnico recomendado

1. Criar `feature-auth` (PIN + biometria + lock de sessao).
2. Adicionar persistencia local com Room e cache offline-first.
3. Introduzir Retrofit + MockWebServer (cenarios de falha, timeout, retry).
4. Fortalecer seguranca (certificate pinning, root/emulator checks, anti-tamper basico).
5. Expandir Robot Pattern para fluxo completo de criacao/edicao/exclusao.
