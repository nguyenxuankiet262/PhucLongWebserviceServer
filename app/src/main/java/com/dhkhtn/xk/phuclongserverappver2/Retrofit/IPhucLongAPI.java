package com.dhkhtn.xk.phuclongserverappver2.Retrofit;

import com.dhkhtn.xk.phuclongserverappver2.Model.AverageRate;
import com.dhkhtn.xk.phuclongserverappver2.Model.Banner;
import com.dhkhtn.xk.phuclongserverappver2.Model.Category;
import com.dhkhtn.xk.phuclongserverappver2.Model.Drink;
import com.dhkhtn.xk.phuclongserverappver2.Model.Feedback;
import com.dhkhtn.xk.phuclongserverappver2.Model.Order;
import com.dhkhtn.xk.phuclongserverappver2.Model.Rating;
import com.dhkhtn.xk.phuclongserverappver2.Model.Store;
import com.dhkhtn.xk.phuclongserverappver2.Model.Token;
import com.dhkhtn.xk.phuclongserverappver2.Model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IPhucLongAPI {
    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") int isServerToken);

    @FormUrlEncoded
    @POST("inserttoken.php")
    Call<Token> insertToken(@Field("phone") String phone,
                            @Field("token") String token,
                            @Field("isServerToken") int isServerToken);
    @FormUrlEncoded
    @POST("insertcategory.php")
    Call<String> insertCategory(@Field("name") String name,
                                @Field("image") String image);
    @FormUrlEncoded
    @POST("insertdrink.php")
    Call<String> insertDrink(@Field("categoryID") int categoryID,
                             @Field("imageHot") String imageHot,
                             @Field("imageCold") String imageCold,
                             @Field("Name") String Name,
                             @Field("Price") int Price
    );
    @FormUrlEncoded
    @POST("getorderbyid.php")
    Observable<List<Order>> getOrderById(@Field("id") String id);
    @FormUrlEncoded
    @POST("getuserbystring.php")
    Observable<List<User>> getUserByString(@Field("string") String string);
    @FormUrlEncoded
    @POST("getrating.php")
    Observable<List<Rating>> getRating(@Field("drinkID") int drinkID);
    @FormUrlEncoded
    @POST("getavgrate.php")
    Call<AverageRate> getAvgRate(@Field("drinkID") int drinkID);
    @FormUrlEncoded
    @POST("updatecategory.php")
    Call<String> updateCategory(@Field("id") String id,
                                @Field("image") String image,
                                @Field("name") String name);
    @FormUrlEncoded
    @POST("updatedrink.php")
    Call<String> updateDrink(@Field("id") int id,
                             @Field("imageHot") String imageHot,
                             @Field("imageCold") String imageCold,
                             @Field("name") String name,
                             @Field("price") int price);
    @FormUrlEncoded
    @POST("updatestatus.php")
    Call<String> updateStatus(@Field("id") int id,
                              @Field("status") int status);
    @FormUrlEncoded
    @POST("updateactiveuser.php")
    Call<String> updateActiveUser(@Field("phone") String phone,
                                  @Field("active") int active);
    @FormUrlEncoded
    @POST("updatefeedback.php")
    Call<String> updateFeedback(@Field("id") int id,
                                @Field("reply") String reply);
    @FormUrlEncoded
    @POST("deletecategory.php")
    Call<String> deleteCategory(@Field("id") int id);

    @FormUrlEncoded
    @POST("deletedrink.php")
    Call<String> deleteDrink(@Field("id") int id);

    @FormUrlEncoded
    @POST("getallorder.php")
    Observable<List<Order>> getOrder(@Field("status") int status,
                                     @Field("storeID") int storeID);
    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuid);
    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();
    @GET("getcategory.php")
    Observable<List<Category>> getCategory();
    @GET("getlocation.php")
    Observable<List<Store>> getLocation();
    @GET("getallfeedback.php")
    Observable<List<Feedback>> getAllFeedback();
    @GET("getalluser.php")
    Observable<List<User>> getAllUser();
}
