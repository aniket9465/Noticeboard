package com.channeli.img.noticeboard.Utilities;

import com.channeli.img.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.channeli.img.noticeboard.ApiRequestBody.LoginRequestBody;
import com.channeli.img.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.channeli.img.noticeboard.ApiRequestBody.TokenBody;
import com.channeli.img.noticeboard.ApiResponseClasses.Filters;
import com.channeli.img.noticeboard.ApiResponseClasses.LoginResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.channeli.img.noticeboard.ApiResponseClasses.UserInfo.UserInfo;
import com.channeli.img.noticeboard.ApiResponseClasses.accessToken;
import com.channeli.img.noticeboard.ApiResponseClasses.notificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("token_auth/obtain_pair/")
    Call<LoginResponse> login(@Body LoginRequestBody credentials);

    @POST("token_auth/refresh/")
    Call<accessToken> refreshToken(@Body RefreshTokenBody refreshToken);

    @POST("api/notifications/token/")
    Call<notificationResponse> notificationToken(@Body TokenBody token, @Header("Authorization") String Authorization);

    @HTTP(method = "DELETE", path = "api/notifications/token/", hasBody = true)
    Call<notificationResponse> DeleteNotificationToken(@Body TokenBody token, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> get_notices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @POST("api/noticeboard/star_read/")
    Call<Void> bookmark_read(@Header("Authorization") String Authorization, @Body BookmarkReadRequestBody body);

    @GET("api/noticeboard/new/{id}/")
    Call<NoticeContentResponse> noticeContent(@Path(value="id") Integer id, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/{id}/")
    Call<NoticeContentResponse> noticeContentExpired(@Path(value="id") Integer id, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter_list/")
    Call<List<Filters>> getFilters(@Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> dateFilter(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page,@Header("Authorization") String Authorization);

    @GET("api/noticeboard/star_filter_view/")
    Call<NoticeListResponse> bookmarkedNotices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> importantNotices(@Query(value = "page") String page,@Query(value = "important") Boolean important, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> importantUnreadNotices(@Query(value = "page") String page,@Query(value = "important") Boolean important,@Query(value = "unread") Boolean unread, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/old/")
    Call<NoticeListResponse> expiredNotices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/old/")
    Call<NoticeListResponse> searchExpiredNotices(@Query(value = "page")  String page , @Query(value = "keyword") String search, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> search_notices(@Query(value = "keyword") String search,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> searchAndDateFilter(@Query(value = "keyword") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> filteredNotices(@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> filterAndDateFilterNotices(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> searchAndFilteredNotices(@Query(value = "keyword") String search,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> searchAndFilterAndDateFilterNotices(@Query(value = "keyword") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> filteredNoticesAll(@Query(value = "main_category") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> filterAndDateFilterNoticesAll(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "main_category") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> searchAndFilteredNoticesAll(@Query(value = "keyword") String search,@Query(value = "v") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> searchAndFilterAndDateFilterNoticesAll(@Query(value = "keyword") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "main_category") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);


    @GET("kernel/who_am_i/")
    Call<UserInfo> getUserInfo(@Header("Authorization") String Authorization);

}
