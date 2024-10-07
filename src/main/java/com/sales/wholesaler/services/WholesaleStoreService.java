package com.sales.wholesaler.services;

import com.sales.dto.AddressDto;
import com.sales.dto.StoreDto;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WholesaleStoreService extends WholesaleRepoContainer {



    @Value("${store.absolute}")
    String storeImagePath;

    @Value("${store.relative}")
    String storeImageRelativePath;

    public AddressDto getAddressObjFromStore(StoreDto storeDto){
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(storeDto.getCity());
        addressDto.setState(storeDto.getState());
        addressDto.setLatitude(storeDto.getLatitude());
        addressDto.setAltitude(storeDto.getAltitude());
        return addressDto;
    }


    @Transactional(rollbackOn = {MyException.class , RuntimeException.class})
    public Map<String, Object> updateStoreBySlug(StoreDto storeDto,User loggedUser) throws IOException {
        Map<String, Object> responseObj = new HashMap<>();
        Store store = getStoreByUserId(loggedUser.getId());
        String slug = store.getSlug();
        storeDto.setStoreSlug(slug);
        String imageName = saveStoreImage(storeDto.getStorePic(), slug);
        if(imageName !=null){
            storeDto.setStoreAvatar(imageName);
        }else{
            storeDto.setStoreAvatar(store.getAvtar());
        }
        int isUpdated = updateStore(storeDto, loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "successfully updated.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "nothing to updated. may be something went wrong");
            responseObj.put("status", 400);
        }
        return responseObj;
    }

    @Transactional(rollbackOn = {MyException.class,RuntimeException.class})
    public int updateStore(StoreDto storeDto, User loggedUser){
        AddressDto address = new AddressDto();
        address.setAddressSlug(storeDto.getAddressSlug());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        int isUpdatedAddress = addressHbRepository.updateAddress(address,loggedUser);
        if(isUpdatedAddress < 1) return isUpdatedAddress;
        return wholesaleStoreHbRepository.updateStore(storeDto,loggedUser);
    }


    @Transactional
    public Store getStoreDetails(String slug){
        return wholesaleStoreRepository.findStoreBySlug(slug);
    }


    public Store getStoreByUserSlug(Integer userId) {
        return wholesaleStoreRepository.findStoreByUserId(userId);
    }

    public Store getStoreByUserId(Integer userId){
        return wholesaleStoreRepository.findStoreByUserId(userId);
    }

    public Integer getStoreIdByUserSlug(Integer userId) {
        return wholesaleStoreRepository.getStoreIdByUserId(userId);
    }


    @Transactional
    public String saveStoreImage(MultipartFile profileImage, String slug) throws MyException, IOException {
        if(profileImage !=null ) {
            String fileOriginalName = profileImage.getOriginalFilename().replaceAll(" ", "_");
            if (!Utils.isValidImage(fileOriginalName)) throw new MyException("Not a valid file.");
            String dirPath = storeImagePath+"/"+slug+"/";
            File dir = new File(dirPath);
            if(!dir.exists()) dir.mkdirs();
            profileImage.transferTo(new File(dirPath+fileOriginalName));
            return fileOriginalName;
        }
        return null;
    }

}
