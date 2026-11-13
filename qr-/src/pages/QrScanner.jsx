import { QRCode } from "react-qrcode-logo";

const QrScanner = ({ token }) => {

  console.log("ScannerQr received token:", token); 

  if (!token) {
    return <p className="text-center text-gray-500">No token available</p>;
  }

  return (
    <div className="flex justify-center p-4 bg-white rounded-lg shadow-sm">
      <QRCode
        value={`ATTENDANCE:${token}`}
        ecLevel="H"
        size={250}
        bgColor="#ffffff"
        fgColor="#111111"
        qrStyle="fluid"
        eyeRadius={10}
        eyeColor="#444444"
        quietZone={10}
      />
    </div>
  );
};

export default QrScanner;