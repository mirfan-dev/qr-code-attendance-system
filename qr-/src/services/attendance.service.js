import { publicAxios } from "./axios.service";

// Scan QR code — records attendance (auto CHECK_IN / CHECK_OUT)
export const scanQr = (qrPayload) => {
  return publicAxios
    .post(`/attendance/scan`, { qrPayload }) // ⭐ Sends JSON object: { "qrPayload": "..." }
    .then((response) => response.data);
};

export const getUserLogs = (userId) => {
  return publicAxios
    .get(`/attendance/user/${userId}`) 
    .then((response) => response.data);
};

export const getAll = () => {
  return publicAxios
    .get(`/attendance`) 
    .then((response) => response.data);
};