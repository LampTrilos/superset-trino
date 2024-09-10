package gr.police.polseal.service.mailer;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.CheckedTemplate;

@CheckedTemplate
public class Templates {

  public static native MailTemplate.MailTemplateInstance sign_code(String code, String compTransactionUdc);

  public static native MailTemplate.MailTemplateInstance transaction_completed(String compTransactionUdc);

}
