# Financial Tracker - Resumo Executivo

Projeto Android de estudo com foco em engenharia de software para contexto financeiro.

## Visao geral

- App simples, com arquitetura escalavel e testavel.
- Fluxo principal em XML (transacoes) e uma tela em Jetpack Compose (dashboard).
- Estrutura modular para separar responsabilidades.

## Stack principal

- Kotlin
- XML + Fragment
- Jetpack Compose (1 tela)
- MVVM
- Koin (DI)
- Coroutines + Flow
- LiveData, StateFlow e SharedFlow
- Testes unitarios + UI Robot Pattern
- Segurança local com EncryptedSharedPreferences + Keystore

## Diferenciais tecnicos

- Clean Architecture com separacao em modulos (`app`, `core`, `domain`, `data`, `feature-*`).
- Modelagem financeira com `BigDecimal` para evitar erro de precisao.
- DI explicita e simples para evolucao de features.
- Base pronta para migracao de fake API para backend real.

## Status atual

- MVP funcional implementado.
- Build e testes unitarios validos.
- Testes UI estruturados (execucao depende de emulador/device).

## Proximos passos

1. Autenticacao com PIN + biometria.
2. Persistencia local com Room.
3. Retrofit + MockWebServer.
4. Hardening de seguranca (pinning/integrity/tamper checks).
