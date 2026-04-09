import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance"; // 우리가 만든 Axios 설정들
import { useNavigate } from "react-router-dom";

function AdminPage() {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  const fetchUsers = async () => {
    try {
      const res = await axiosInstance.get("/api/admin/users");
      setUsers(res.data);
    } catch (err) {
      console.error("사용자 목록 불러오기 실패:", err);
      alert("사용자 정보를 불러오는데 실패했습니다.");
      navigate("/"); // 실패하면 홈페이지로 이동
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []); //최초 1번 실행

  return (
    <div className="home">
      <h2>Admin 페이지입니다!</h2>
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            {user.username} ({user.role})
          </li>
        ))}
      </ul>
    </div>
  );
}

export default AdminPage;
