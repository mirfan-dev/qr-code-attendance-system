
import { useEffect, useState } from "react";
import { getAll } from "../services/attendance.service";
import { Spinner } from "react-bootstrap";
import { toast } from "react-toastify";

const Dashboard = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (!userId) {
      toast.error("User not found. Please log in.");
      setLoading(false);
      return;
    }

    getAll(userId)
      .then((data) => setLogs(data))
      .catch(() => toast.error("Failed to load attendance history"))
      .finally(() => setLoading(false));
  }, [userId]);

  const formatTime = (timeString) => {
    if (!timeString) return "—";
    return new Date(timeString).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading)
    return (
      <div className="flex justify-center mt-10">
        <Spinner animation="border" />
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 px-3 py-8 sm:px-6">
      <div className="mx-auto w-full max-w-6xl">
        <h1 className="text-2xl sm:text-3xl font-bold mb-6 text-center text-gray-800">
          Attendance Dashboard
        </h1>

        {logs.length === 0 ? (
          <p className="text-center text-gray-500 bg-white p-6 rounded-lg shadow-sm">
            No records found.
          </p>
        ) : (
          <div className="overflow-x-auto rounded-xl bg-white shadow-sm border border-gray-200">
            <table className="w-full min-w-[600px] border-collapse text-center text-sm">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="px-3 py-4">Date</th>
                  <th className="px-3 py-4">Check In</th>
                  <th className="px-3 py-4">Check Out</th>
                  <th className="px-3 py-4">Name</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-200">
                {logs.map((log) => (
                  <tr key={log.id} className="hover:bg-gray-50">
                    <td className="px-3 py-4">{log.date}</td>
                    <td className="px-3 py-4 font-medium text-green-600">
                      {formatTime(log.checkInTime)}
                    </td>
                    <td className="px-3 py-4 font-medium text-blue-600">
                      {formatTime(log.checkOutTime)}
                    </td>
                    <td className="px-3 py-4 text-gray-700">
                      {log.userName}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
