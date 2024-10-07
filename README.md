# Send Email Using Brevo

Brevo (formerly Sendinblue) is a comprehensive email marketing and automation service that offers SMTP relay and transactional email services.


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
  
  * Description: Sends an email using Brevo's SMTP API. You can optionally specify a template ID.
  Request Parameters:

  * templateId (optional, Long): The ID of the email template to use.

  @RequestParam(required = false) Long templateId,@RequestBody:

Notes:

  If templateId is provided, the email will be sent using the specified template. the request body looks like this without param templateId
  ```
{
  "emailTo": "recipient@example.com",
  "subject": "Your Subject",
  "params": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```
  If templateId is not specified, the email will be sent without using any template, if that is the case you will need to specify the content.
  * it will look like this 

```
{
  "emailTo": "recipient@example.com",
  "subject": "Your Subject",
  "content": "<h1>Hello, World!</h1>",
  "params": {
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

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

# Manage Email Templates

a. Create a New Email Template

  Endpoint: POST /api/v1/create
  Description: Creates a new email template.
  Request Body:

```
 {
  "templateName": "WelcomeTemplate",
  "subject": "Welcome to Our Service",
  "htmlContent": "<h1>Welcome, {{params.firstName}}!</h1>",
  "replyTo": "support@example.com",
  "isActive": true,
  "attachmentUrl": "https://example.com/attachment.pdf", // optional to include
  "toField": "{{params.email}}"
}
```
Response:
```
{
  "id": 123,
  "message": "Template created successfully."
}
```
b. Update an Existing Email Template

  Endpoint: PUT `/api/v1/update`
  
  Description: Updates an existing email template.
  Request Parameters:
      templateId (required, Long): The ID of the template to update.
  Request Body:

```
{
  "templateName": "UpdatedTemplateName",
  "subject": "Updated Subject",
  "htmlContent": "<h1>Updated Content</h1>",
  "replyTo": "support@example.com",
  "isActive": false,
  "attachmentUrl": "https://example.com/new-attachment.pdf", //optinal to include
  "toField": "{{params.email}}"
}
```

Response:

```
{
  "message": "Template updated successfully."
}

```
c. Fetch All Email Templates

  Endpoint: GET `/api/v1/list`

  Description: Retrieves all email templates with optional filtering.
  Request Parameters:
      isActive (optional, Boolean): Filter by active/inactive templates. Default is true.
      limit (optional, Integer): Number of templates to return. Default is 50.
      offset (optional, Integer): Index of the first template. Default is 0.

d. Activate an Email Template

  Endpoint: PUT `/api/v1/activate`

  Description: Activates a deactivated email template.
  Request Parameters:
      templateId (required, Long): The ID of the template to activate.
        
Response:

```
{
  "message": "Template activated successfully."
}

```

e. Deactivate an Email Template

  Endpoint: PUT `/api/v1/deactivate`

  Description: Deactivates an active email template.
  Request Parameters:
      templateId (required, Long): The ID of the template to deactivate.
        
Response:

```
{
  "message": "Template deactivated successfully."
}

```

f. Delete an Email Template

  Endpoint: DELETE `/api/v1/delete`

  Description: Deletes an email template.
  Request Parameters:
      templateId (required, Long): The ID of the template to delete.
  ```

{
  "message": "Template deleted successfully."
}
  ```

    

after an email was send there is a webhook configured in brevo, brevo will tracking the email and send a webhook on current status
of the email, weather it was request, delivered or opened and send the data to `/api/v1/webhook/email-event`, and this program takes the data and maps it a coordenly to correct places

# Deployment
- the application must be deployed in order to see the webhook in action, since brevo only sends out the webhooks notification to a public address

you can register a webhook in brevo by [clicking here](https://app-smtp.brevo.com/webhook)
