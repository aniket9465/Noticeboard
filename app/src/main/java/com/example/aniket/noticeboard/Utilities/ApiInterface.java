package com.example.aniket.noticeboard.Utilities;

import com.example.aniket.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.example.aniket.noticeboard.ApiRequestBody.LoginRequestBody;
import com.example.aniket.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.example.aniket.noticeboard.ApiResponseClasses.Filters;
import com.example.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.example.aniket.noticeboard.ApiResponseClasses.LoginResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.example.aniket.noticeboard.ApiResponseClasses.accessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("api/noticeboard/token_auth/obtain_pair/")
    Call<LoginResponse> login(@Body LoginRequestBody credentials);

    @POST("api/noticeboard/token_auth/refresh/")
    Call<accessToken> refreshToken(@Body RefreshTokenBody refreshToken);

    @GET("api/noticeboard/notice_list/{page}/")
    Call<NoticeListResponse> get_notices(@Path(value = "page") String page, @Header("access_token") String access_token);

    @POST("api/noticeboard/bookmark_read/")
    Call<Void> bookmark_read(@Header("access_token") String access_token, @Body BookmarkReadRequestBody body);

    @GET("api/noticeboard/notice/{id}/")
    Call<NoticeContentResponse> noticeContent(@Path(value="id") String id, @Header("access_token") String access_token);

    @GET("/api/noticeboard/filter_list/")
    Call<FiltersList> getFilters(@Header("access_token") String access_token);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> dateFilter(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page,@Header("access_token") String access_token);

    @GET("api/noticeboard/bookmarked_notices/{page}/")
    Call<NoticeListResponse> bookmarkedNotices(@Path(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/expired_notices/{page}/")
    Call<NoticeListResponse> expiredNotices(@Path(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> filteredNotices(@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/filter/")
    Call<NoticeListResponse> filterAndDateFilterNotices(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/search/")
    Call<NoticeListResponse> search_notices(@Query(value = "search") String search,@Query(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/search/")
    Call<NoticeListResponse> searchAndFilteredNotices(@Query(value = "search") String search,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/search/")
    Call<NoticeListResponse> searchAndFilterAndDateFilterNotices(@Query(value = "search") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("access_token") String access_token);

    @GET("api/noticeboard/search/")
    Call<NoticeListResponse> searchAndDateFilter(@Query(value = "search") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page, @Header("access_token") String access_token);

}
