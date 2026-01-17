package sales.application.sales;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"sales.application.sales.wholesaler.controller" ,
        "sales.application.sales.admin.controller",
        "sales.application.sales.admin.services",
        "sales.application.sales.chats.controllers",
        "sales.application.sales.chats.services",
        "sales.application.sales.chats.repositories",
})
class SalesApplicationTests {

}
