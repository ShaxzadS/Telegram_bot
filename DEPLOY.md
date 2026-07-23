# Deploy

## Docker

Build image:

```bash
docker build -t shaxzadbot .
```

Run bot:

```bash
docker run -d --name shaxzadbot --restart unless-stopped \
  -e BOT_NAME=Shaxzad_bot \
  -e BOT_KEY=your_telegram_bot_token \
  shaxzadbot
```

Local run on Windows PowerShell:

```powershell
$env:BOT_NAME="Shaxzad_bot"
$env:BOT_KEY="your_telegram_bot_token"
.\mvnw.cmd spring-boot:run
```

By default the app connects to `jdbc:postgresql://localhost:5432/ATMDatabase`
with `postgres` / `2231`. Override `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` if your database
is elsewhere.

View logs:

```bash
docker logs -f shaxzadbot
```

Restart after changes:

```bash
docker stop shaxzadbot
docker rm shaxzadbot
docker build -t shaxzadbot .
docker run -d --name shaxzadbot --restart unless-stopped \
  -e BOT_NAME=Shaxzad_bot \
  -e BOT_KEY=your_telegram_bot_token \
  shaxzadbot
```

## Notes

Do not store the Telegram bot token in Git. Pass it through `BOT_KEY` on the server.
