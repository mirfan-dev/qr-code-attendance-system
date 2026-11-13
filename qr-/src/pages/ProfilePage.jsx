import { useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { scanQr, getUserLogs } from "../services/attendance.service";
import { toast } from "react-toastify";
import QrScanner from "./QrScanner";

const ProfilePage = () => {
  const location = useLocation();
  const token = location.state?.token || localStorage.getItem("qrToken");
  const userId = localStorage.getItem("userId");

  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("LOADING"); 

  useEffect(() => {
    if (!userId) {
      setStatus("NOT_CHECKED_IN");
      return;
    }

    getUserLogs(userId)
      .then((logs) => {
        const today = new Date().toISOString().split("T")[0]; 
        const todayLog = logs.find((log) => log.date === today);

        if (todayLog) {
          if (todayLog.checkOutTime) {
            setStatus("CHECKED_OUT");
          } else if (todayLog.checkInTime) {
            setStatus("CHECKED_IN");
          }
        } else {
          setStatus("NOT_CHECKED_IN");
        }
      })
      .catch((err) => {
        console.error("Failed to load attendance status", err);
        setStatus("NOT_CHECKED_IN");
      });
  }, [userId]);

  // 2. Handle the button click
  const handleScan = async () => {
    if (!token) return;
    setLoading(true);

    try {
      const result = await scanQr(`ATTENDANCE:${token}`);
      
      if (result.status === "CHECKED_IN") {
        setStatus("CHECKED_IN");
        toast.success("Checked In successfully!");
      } else if (result.status === "CHECKED_OUT") {
        setStatus("CHECKED_OUT");
        toast.success("Checked Out successfully!");
      }
    } catch (err) {
      toast.error(err.response?.data?.message || "Scan failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">
        Your Attendance QR
      </h1>

      {/* QR Code Container */}
      <div className="rounded-xl bg-white p-4 shadow-sm mb-8">
        <QrScanner token={token} />
      </div>

      {/* Status & Buttons */}
      <div className="flex flex-col items-center">
        
        {status === "LOADING" && (
          <p className="text-gray-500">Checking your status...</p>
        )}

        {status === "NOT_CHECKED_IN" && (
          <button
            onClick={handleScan}
            disabled={loading}
            className="bg-green-600 hover:bg-green-700 text-white font-semibold px-8 py-3 rounded-lg transition-colors duration-300 disabled:opacity-50"
          >
            {loading ? "Processing..." : "Simulate Check-In"}
          </button>
        )}

        {status === "CHECKED_IN" && (
          <button
            onClick={handleScan}
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-3 rounded-lg transition-colors duration-300 disabled:opacity-50"
          >
            {loading ? "Processing..." : "Simulate Check-Out"}
          </button>
        )}

        {status === "CHECKED_OUT" && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-6 py-3 rounded-lg font-semibold text-center">
             Attendance Completed for Today
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;