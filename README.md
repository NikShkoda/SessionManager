# SessionManager
Test app for Overplay

- UI, as small as it is, is done via Compose. Added loading and error states in Compose just for better UX
- Counting sessions is done via Room database, WorkManager and viewModel with Flow and State
- Examples for sensor code was taken from https://github.com/KalebKE/FSensor, but rewritten to Kotlin, including some changes to code, and minimized to 4 files
- DI for the project is Hilt(used to combine domain-data-presentation modules, worker and gyroscope sensor)
