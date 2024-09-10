package gr.police.polseal.dto;

import io.quarkus.qute.TemplateExtension;

import java.util.stream.Collectors;

@TemplateExtension
class IdtoExtensions {

  static String itemCodeFriendly(ITransactionResDto idto) { 
    String str = idto.getItemCode();
    return str.substring(5, 7) + " " + str.substring(12, 15);
  }

  static String chargedAccount(ITransactionResDto idto) { 
    String str = "Αποθήκη Γενικής";
    if (idto.getProxyDepUnitTo() != null) {
      str = idto.getProxyDepUnitTo().getName();
    }
    return str;
  }  
  
  static String serial(ITransactionResDto idto) { 
    String str = "-";
    if (idto.getItemJson() != null) {
      //System.out.println(idto.getItemJson() );
      str = idto.getItemJson().containsKey("serialNumber") ? ((String) idto.getItemJson().get("serialNumber")) : "-";
    }
    return str;
  }

  static String childrenTransactionsSerials(ITransactionResDto idto) {
    return idto.getChildrenTransactions().stream().map(tr -> serial(tr)).filter(s -> !s.equals("-")).collect(Collectors.joining(", "));
  }

  static String arithmosMitroouFriendly(UserDto userDto) {
    return "(" + userDto.getArithmosMitroou() + ")";
  }

}
