package com.channeli.aniket.noticeboard.Utilities;

import com.channeli.aniket.noticeboard.ApiRequestBody.BookmarkReadRequestBody;
import com.channeli.aniket.noticeboard.ApiRequestBody.LoginRequestBody;
import com.channeli.aniket.noticeboard.ApiRequestBody.RefreshTokenBody;
import com.channeli.aniket.noticeboard.ApiResponseClasses.Filters;
import com.channeli.aniket.noticeboard.ApiResponseClasses.FiltersList;
import com.channeli.aniket.noticeboard.ApiResponseClasses.LoginResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.NoticeContentResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.NoticeListResponse;
import com.channeli.aniket.noticeboard.ApiResponseClasses.accessToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("token_auth/obtain_pair/")
    Call<LoginResponse> login(@Body LoginRequestBody credentials);

    @POST("token_auth/refresh/")
    Call<accessToken> refreshToken(@Body RefreshTokenBody refreshToken);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> get_notices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @POST("api/noticeboard/star_read/")
    Call<Void> bookmark_read(@Header("Authorization") String Authorization, @Body BookmarkReadRequestBody body);

    @GET("api/noticeboard/new/{id}/")
    Call<NoticeContentResponse> noticeContent(@Path(value="id") Integer id, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/filter_list/")
    Call<List<Filters>> getFilters(@Header("Authorization") String Authorization);

    @GET("api/noticeboard/date_filter_view/")
    Call<NoticeListResponse> dateFilter(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page,@Header("Authorization") String Authorization);

    @GET("api/noticeboard/star_filter_view/")
    Call<NoticeListResponse> bookmarkedNotices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/old/")
    Call<NoticeListResponse> expiredNotices(@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/old/")
    Call<NoticeListResponse> searchExpiredNotices(@Query(value = "page")  String page , @Query(value = "search") String search, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> filteredNotices(@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> filterAndDateFilterNotices(@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new")
    Call<NoticeListResponse> search_notices(@Query(value = "search") String search,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> searchAndFilteredNotices(@Query(value = "search") String search,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> searchAndFilterAndDateFilterNotices(@Query(value = "search") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "banner") String banner,@Query(value = "page") String page, @Header("Authorization") String Authorization);

    @GET("api/noticeboard/new/")
    Call<NoticeListResponse> searchAndDateFilter(@Query(value = "search") String search,@Query(value="start") String start,@Query(value = "end") String end,@Query(value = "page") String page, @Header("Authorization") String Authorization);

}
