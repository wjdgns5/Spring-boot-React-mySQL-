// 자동적으로 Axios 요청을 할 때, Header에 쿠키를 담아서 보내도록 한다.
import axios from "axios";
import Cookies from "js-cookie";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // Http only 설정 쿠키 같은 인증 정보(credentials)를 같이 보내도록 허용
});

// interceptors : 요청이 서버로 나가기 직전에 중간에서 가로채서 처리하는 기능
axiosInstance.interceptors.request.use(
  (config) => {
    // config : 이번에 보내려는 요청 정보 (url, method, headers, data, params) 묶음
    const token = Cookies.get("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      // 엑세스 토큰 앞에 Bearer를 붙여야 한다. (Authorization: Bearer 토큰값)
    }
    return config;
  },
  (error) => Promise.reject(error),
);

axiosInstance.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalRequest = err.config;

    // || err.response?.status === 403
    if (
      (err.response?.status === 401 || err.response?.status === 403) &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      try {
        const res = await axios.post(
          "http://localhost:8080/api/auth/refresh",
          {},
          { withCredentials: true },
        );

        const newAccessToken = res.data.accessToken;

        Cookies.set("accessToken", newAccessToken, {
          expires: 0.021,
          path: "/",
        });

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        console.log("Refresh 실패:", refreshError);
        window.location.href = "/login";
      }
    }

    return Promise.reject(err);
  },
);

export default axiosInstance;
