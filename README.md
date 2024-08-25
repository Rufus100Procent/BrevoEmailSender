# Send Email Using Brevo

This project demonstrates the integration of Brevo (formerly Sendinblue) with a Java Spring Boot application. Brevo is a comprehensive email marketing and automation service that offers SMTP relay and transactional email services.

## Requirements

- **Java:** JDK 11 or higher
- **IDE:** IntelliJ IDEA recommended
- **Brevo Account:** Required for SMTP API keys

## Getting Started
1. open terminal
2. git clone 
3. open with an idea

### 2. Obtain SMTP API Keys from Brevo

1. Sign up for a Brevo account at [brevo.com](https://www.brevo.com).
2. Navigate to **SMTP & API** section under your account settings. [Click here](https://app.brevo.com/settings/keys/api)
3. Generate and copy your SMTP API key.
4. Generate or use the master password [Click here](https://app.brevo.com/settings/keys/smtp) and add in application.properties "spring.mail.password"


# API Endpoints

- POST `/api/v1/send-email `: Sends an email using Brevo's SMTP API.
- POST `/api/v1/webhook/email-event`: Receives and processes Brevo webhooks.
- GET `/api/v1/emails`: here you retrieve a formated data
```
{
  "Status": "opened",
  "Mirror Link": "https://app-smtp.brevo.com/log/preview/c345a7c3-dca1-44cf-af10-b91e74b68897",
  "From": "example@gmail.com",
  "To": "example@outlook.com",
  "Date": "2024-08-24 20:58:55",
  "Subject": "test subject"
}
```

- GET `/api/v1/emails/raw-data`: Retrieves raw email data including full history.

```
{
  "id": "1136738",
  "emailTo": "example@outlook.com",
  "messageId": "an#15239426342450338043",
  "tag": "[\"welcame\"]",
  "date": "2024-08-24 20:58:55",
  "event": "opened",
  "subject": "hello",
  "sendingIp": "::",
  "senderEmail": "example@gmail.com",
  "mirrorLink": "https://app-smtp.brevo.com/log/preview/c345a7c3-dca1-44cf-af10-b91e74b68897",
  "content": "<h1>Hello,</h1><p>Thank you for signing up for our service. We're glad to have you!</p>",
  "params": {
    "firstName": "John",
    "lastName": "Doe"
  },
  "opened": true,
  "history": [
    {
      "id": "1136738",
      "email": "example@outlook.com",
      "date": "2024-08-24 20:54:54",
      "event": "request",
      "sendingIp": "77.32.148.24",
      "mirrorLink": "https://app-smtp.brevo.com/log/preview/4f0e0c65-51f5-49c4-b7f2-bc9ea0a85c01"
    },
    {
      "id": "1136738",
      "email": "example@outlook.com",
      "date": "2024-08-24 20:54:56",
      "event": "delivered",
      "sendingIp": "77.32.148.24",
      "mirrorLink": null
    },
    {
      "id": "1136738",
      "email": "",
      "date": "2024-08-24 20:57:35",
      "event": "unique_opened",
      "sendingIp": "::",
      "mirrorLink": "https://app-smtp.brevo.com/log/preview/c345a7c3-dca1-44cf-af10-b91e74b68897"
    },
    {
      "id": "1136738",
      "email": "",
      "date": "2024-08-24 20:58:55",
      "event": "opened",
      "sendingIp": "::",
      "mirrorLink": "https://app-smtp.brevo.com/log/preview/c345a7c3-dca1-44cf-af10-b91e74b68897"
    }
  ]
}
```
after an email was send there is a webhook configured in brevo, brevo will tracking the email and send a webhook on current status
of the email, weather it was request, delivered or opened and send the data to `/api/v1/webhook/email-event`, and this program takes the data and maps it a coordenly to correct places

# Deployment
- the application must be deployed in order to see the webhook in action, since brevo only sends out the webhooks notification to a public address

you can register a webhook in brevo by [clicking here](https://app-smtp.brevo.com/webhook)