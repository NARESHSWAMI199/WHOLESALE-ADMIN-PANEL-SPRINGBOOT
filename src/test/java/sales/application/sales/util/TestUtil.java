package sales.application.sales.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.admin.repositories.*;
import com.sales.entities.*;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sales.application.sales.testglobal.GlobalConstantTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class TestUtil {

    private final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For JSON parsing/creation

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ServicePlanRepository servicePlanRepository;

    @Autowired
    protected WholesalerPlansRepository wholesalePlansRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected ItemCategoryRepository itemCategoryRepository;

    @Autowired
    protected ItemSubCategoryRepository itemSubCategoryRepository;

    @Autowired
    protected StoreCategoryRepository storeCategoryRepository;

    @Autowired
    protected StoreSubCategoryRepository storeSubCategoryRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected CityRepository cityRepository;

    @Autowired
    protected StateRepository stateRepository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected SupportEmailsRepository supportEmailsRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    protected  PermissionRepository permissionRepository;


    @Autowired
    protected  StorePermissionsRepository storePermissionRepository;

    @Autowired
    protected WholesalePermissionRepository wholesalePermissionRepository;

    protected  Integer storeId;

    protected String storeSlug;

    protected String selfSlug;



    protected String createRandomEmail(){
       return UUID.randomUUID()+"@sales.com";
    }

    protected String extractTokenFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class); // Create a TokenResponse class
        return tokenResponse.getToken();
    }



    protected String extractSlugFromResponseViaRes(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestResResponse resResponse = objectMapper.readValue(responseBody, TestResResponse.class); // Create a TokenResponse class
        return (String) resResponse.getRes().get("slug");
    }


    protected String extractSlugFromResponseViaUser(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUser testUser = objectMapper.readValue(responseBody, TestUser.class); // Create a TokenResponse class
        return (String) testUser.getUser().get("slug");
    }

    protected Map<String,Object> extractUserFromResponseViaUser(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUser testUser = objectMapper.readValue(responseBody, TestUser.class); // Create a TokenResponse class
        return testUser.getUser();
    }


    protected List extractCategoryListFromResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();
        List categoryResponse = objectMapper.readValue(responseBody, List.class); // Create a TokenResponse class
        return categoryResponse;
    }

    @Getter
    @Setter
    private static class TokenResponse {
        private String token;
    }



    @Getter
    @Setter
    private static class TestResResponse {
        private Map<String,Object> res;
    }

    @Getter
    @Setter
    private static class TestUser {
        private Map<String,Object> user;
    }


    protected State createState() {
        State state = State.builder()
                .stateName("Rajasthan")
                .status("A")
                .build();
        return stateRepository.save(state);
    }

    protected City createCity() {
        State state = createState();
        City city = City.builder()
                .cityName("Jaipur")
                .stateId(state.getId())
                .status("A")
                .build();
        return cityRepository.save(city);
    }


    protected StoreCategory createStoreCategory() {
        String slug = UUID.randomUUID().toString();
        StoreCategory storeCategory  = StoreCategory.builder()
                .category("Electronic")
                .slug(slug)
                .isDeleted("N")
                .icon("abc.png")
                .build();
        return storeCategoryRepository.save(storeCategory);
    }


    protected StoreSubCategory createStoreSubCategory() {
        StoreCategory storeCategory = createStoreCategory();
        String slug = UUID.randomUUID().toString();
        StoreSubCategory storeSubCategory  = StoreSubCategory.builder()
                .categoryId(storeCategory.getId())
                .subcategory("Laptop")
                .slug(slug)
                .isDeleted("N")
                .icon("abc.png")
                .build();
        return storeSubCategoryRepository.save(storeSubCategory);
    }



    public Address createAddress() {
        City city = createCity();
        String slug = UUID.randomUUID().toString();
        Address address = Address.builder()
                .street("Test")
                .state(city.getStateId())
                .city(city.getId())
                .slug(slug)
                .build();
        return addressRepository.save(address);
    }



    public Store createStore() {
        Address address = createAddress();
        StoreCategory storeCategory = createStoreCategory();
        StoreSubCategory storeSubCategory = createStoreSubCategory();
        String slug = UUID.randomUUID().toString();
        Store store = Store.builder()
                .slug(slug)
                .storeName("Test store")
                .storeCategory(storeCategory)
                .storeSubCategory(storeSubCategory)
                .avtar("abc.png")
                .isDeleted("N")
                .address(address)
                .phone(getRandomMobileNumber())
                .build();
        return storeRepository.save(store);
    }


    public ServicePlan createServicePlan(Date currentTime) {
        ServicePlan servicePlan = ServicePlan.builder()
                .name("Test Service plan")
                .slug(UUID.randomUUID().toString())
                .createdAt(currentTime.getTime())
                .price(101L)
                .discount(0L)
                .months(6)
                .updatedAt(currentTime.getTime())
                .createdBy(1)
                .updatedBy(1)
                .build();
        return servicePlanRepository.save(servicePlan);
    }

    public User createUser(String slug,String email, String password ,String userType) {
        String random  = getRandomMobileNumber();
        User user = User.builder()
                .username("naresh")
                .slug(slug)
                .userType(userType)
                .email(email)
                .password(password)
                .status("A")
                .isDeleted("N")
                .contact(random)
                .build();
        return userRepository.save(user);
    }

    public WholesalerPlans createWholesalePlan(String slug,User user,Date currentTime,Date futureDate,ServicePlan servicePlan){
        WholesalerPlans wholesalerPlans = WholesalerPlans.builder()
                .userId(user.getId())
                .createdAt(currentTime.getTime())
                .expiryDate(futureDate.getTime())
                .isExpired(false)
                .slug(slug)
                .servicePlan(servicePlan)
                .build();
        return wholesalePlansRepository.save(wholesalerPlans);
    }

    public String loginUser(String userType) throws Exception {
        String email = UUID.randomUUID()+"@mocktest.in";
        String password = UUID.randomUUID().toString();
        String slug = UUID.randomUUID().toString();
        Date currentTime = new Date();
        Date futureDate = new Date();
        futureDate.setMonth(currentTime.getMonth() + 12);
        ServicePlan servicePlan = createServicePlan(currentTime);
        User user = createUser(slug,email,password,userType);
        WholesalerPlans wholesalerPlans = createWholesalePlan(slug,user,currentTime,futureDate,servicePlan);
        user.setActivePlan(wholesalerPlans.getId());
        selfSlug = slug;
        userRepository.save(user);
        if(userType.equals(GlobalConstantTest.WHOLESALER)){
            Store store = createStore();
            store.setUser(user);
            storeRepository.save(store);
            storeId = store.getId();
            storeSlug = store.getSlug();
            Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(email, password);
            return loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        }
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(email, password);
        return loggedUserResponse.get(ConstantResponseKeys.TOKEN);
    }

    public Map<String,String> getLoginBeaverSlugAndToken(String email, String password) throws Exception {
        String json = """
                    {
                        "email" : "{email}",
                        "password": "{password}"
                    }
                """
                .replace("{email}",email).replace("{password}",password);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        Map<String,String> response = new HashMap<>();
        response.put("slug", extractSlugFromResponseViaUser(result));
        response.put(ConstantResponseKeys.TOKEN, extractTokenFromResponse(result));
        return response;
    }



    public Map<String,String> getWholesaleLoginBeaverSlugAndToken(String email, String password) throws Exception {
        String json = """
                    {
                        "email" : "{email}",
                        "password": "{password}"
                    }
                """
                .replace("{email}",email).replace("{password}",password);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        Map<String,String> response = new HashMap<>();
        response.put("slug", extractSlugFromResponseViaUser(result));
        response.put(ConstantResponseKeys.TOKEN, extractTokenFromResponse(result));
        return response;
    }




    public String getRandomMobileNumber(){
        Random random  = new Random();
        String randomMobileNumber = "9";
        for (int i = 0; i < 9; i++) {
            int randomNumber = random.nextInt(9); // Generates any integer (positive or negative)
            randomMobileNumber += randomNumber;
        }
        return randomMobileNumber;
    }


    public MockMultipartFile getImageMultipartFileToUpload(String parameterName) throws IOException {
        ClassPathResource resource =
                new ClassPathResource(GlobalConstantTest.IMAGE_FOLDER_PATH_TEST+ GlobalConstant.PATH_SEPARATOR+GlobalConstantTest.IMAGE_NAME_TEST);
        logger.info("The file path is : {}",resource.getURL());
        return new MockMultipartFile(
                parameterName,
                GlobalConstantTest.IMAGE_NAME_TEST,
                MediaType.IMAGE_PNG_VALUE,
                resource.getInputStream()
        );

    }


    protected ItemCategory createItemCategory() {
        String slug = UUID.randomUUID().toString();
        ItemCategory itemCategory  = ItemCategory.builder()
               .category("Electronic")
               .slug(slug)
               .isDeleted("N")
               .icon("abc.png")
               .build();
       return itemCategoryRepository.save(itemCategory);
    }


    protected ItemSubCategory createItemSubCategory() {
        ItemCategory itemCategory = createItemCategory();
        String slug = UUID.randomUUID().toString();
        ItemSubCategory itemSubCategory  = ItemSubCategory.builder()
                .categoryId(itemCategory.getId())
                .subcategory("Laptop")
                .slug(slug)
                .isDeleted("N")
                .icon("abc.png")
                .build();
        return itemSubCategoryRepository.save(itemSubCategory);
    }


    public Item createItem(Integer storeId){
        ItemCategory itemCategory = createItemCategory();
        ItemSubCategory itemSubCategory = createItemSubCategory();
        String slug = UUID.randomUUID().toString();
        Item item = Item.builder()
                .wholesaleId(storeId)
                .slug(slug)
                .name("Test item")
                .price(100)
                .capacity(100F)
                .discount(0)
                .isDeleted("N")
                .inStock("Y")
                .status("A")
                .label(GlobalConstantTest.ITEM_LABEL_NEW)
                .itemSubCategory(itemSubCategory)
                .itemCategory(itemCategory)
                .build();
        return itemRepository.save(item);
    }


    public void createSupportEmail () {
        boolean exists = false;
        for (SupportEmail supportEmail : supportEmailsRepository.findAll()) {
            if(supportEmail.getSupportType().equals("SUPPORT")){
                return;
            }
        }
        if(!exists){
            SupportEmail supportEmail = SupportEmail.builder()
                    .email(createRandomEmail())
                    .supportType("SUPPORT")
                    .passwordKey("test")
                    .build();
            supportEmailsRepository.save(supportEmail);
        }
    }


    public Group createGroup() {
        Group group = Group.builder()
                .name("Test group")
                .slug(UUID.randomUUID().toString())
                .build();
        return groupRepository.save(group);
    }

    public UserGroups assignGroup(Integer userId,Integer groupId){
        UserGroups userGroups = UserGroups.builder()
                .groupId(groupId)
                .userId(userId)
                .build();
        return userGroupRepository.save(userGroups);
    }


    public Permission createPermission(){
        Permission permission = Permission.builder()
                .permission("Test")
                .permissionFor("Edit")
                .accessUrl("test.com")
                .build();
        return permissionRepository.save(permission);
    }


    public StorePermissions createStorePermission(){
        StorePermissions permission = StorePermissions.builder()
                .permission("Test")
                .permissionFor("Edit")
                .accessUrl("test.com")
                .defaultPermission("Y")
                .build();
        return storePermissionRepository.save(permission);
    }

    public WholesalerPermissions createWholesalerPermission(Integer userId){
        Permission permission = createPermission();
        WholesalerPermissions wholesalerPermissions = WholesalerPermissions.builder()
                .permissionId(permission.getId())
                .userId(userId)
                .build();
        return wholesalePermissionRepository.save(wholesalerPermissions);
    }


}
