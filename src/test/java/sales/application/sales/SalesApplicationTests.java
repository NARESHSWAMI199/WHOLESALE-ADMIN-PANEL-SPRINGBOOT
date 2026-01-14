package sales.application.sales;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"sales.application.sales.wholesaler.controller" ,
        "sales.application.sales.admin.controller",
        "sales.application.sales.admin.services",
        "sales.application.sales.chats.controller",
        "sales.application.sales.chat.services",
        "sales.application.sales.chat.repositories",
})
class SalesApplicationTests {

}
