curl -X POST "http://localhost:8081/event" \
  -H "Content-Type: application/json" \
  -d '{"eventId":"259d39e6-9b5f-4b26-bc2e-8c6d87ef14a0","eventType":"EMAIL","destination":"email-test@gmail.com","message":"Random event notification"}'

curl -X POST "http://localhost:8081/event" \
-H "Content-Type: application/json" \
-d '{"eventId":"259d39e6-9b5f-4b26-bc2e-8c6d87ef14a0","eventType":"SLACK","destination":"email-test@gmail.com","message":"Random event SLACK notification"}'

curl -X POST "http://localhost:8081/event" \
  -H "Content-Type: application/json" \
  -d '{"eventId":"259d39e6-9b5f-4b26-bc2e-8c6d87ef14a0","eventType":"DISCORD","destination":"email-test@gmail.com","message":"Random event  DISCORD notification"}'