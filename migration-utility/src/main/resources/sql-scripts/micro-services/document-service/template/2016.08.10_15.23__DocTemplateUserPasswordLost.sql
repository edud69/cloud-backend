INSERT INTO document_templates (template_key, classification) values ('app.cloud.mail.user.lostPassword.templateKey', 'EMAIL');

INSERT INTO email_document_templates (id, subject, body) values (
(select id from document_templates where template_key = 'app.cloud.mail.user.lostPassword.templateKey' and classification = 'EMAIL'),
'Password Restore',
REPLACE('Click on the link to reset your password : <a href="#{WEBSITE_ROOT_URL}/password/restore?token=#{CONFIRMATION_TOKEN}&email=#{USER_EMAIL}">Click here</a>.', '#{', '${')
);