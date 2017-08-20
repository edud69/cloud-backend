INSERT INTO document_templates (template_key, classification) values ('template.email.user.subscription.invitation', 'EMAIL');
INSERT INTO document_templates (template_key, classification) values ('template.email.user.subscription.activation', 'EMAIL');

INSERT INTO email_document_templates (id, subject, body) values (
(select id from document_templates where template_key = 'template.email.user.subscription.invitation' and classification = 'EMAIL'),
'Invidation',
REPLACE('Click on the link to activate your account : <a href="#{WEBSITE_ROOT_URL}/signup/confirm?token=#{CONFIRMATION_TOKEN}&email=#{USER_EMAIL}">Click here</a>.', '#{', '${')
);

INSERT INTO email_document_templates (id, subject, body) values (
(select id from document_templates where template_key = 'template.email.user.subscription.activation' and classification = 'EMAIL'),
'Account activated',
'Your account has been activated.'
);