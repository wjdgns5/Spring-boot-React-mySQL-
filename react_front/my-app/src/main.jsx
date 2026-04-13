import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";

// 아까 만든 Redux 중앙 저장소
import { store } from "./store/index.js";

// React와 Redux를 연결해주는 중간 다리
// ex) Redux store를 App 전체에 공급해주는 컴포넌트
import { Provider } from "react-redux";

createRoot(document.getElementById("root")).render(
  // Provider store={store} : 이 App 전체는 store라는 Redux 저장소를 사용하겠습니다.
  <Provider store={store}>
    <App />
  </Provider>,
);
