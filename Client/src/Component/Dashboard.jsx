import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Dashboard = () => {
  const token = localStorage.getItem('token');
  const [userDetails, setUserDetails] = useState(null);
  const [avatarUrl, setAvatarUrl] = useState('');

  useEffect(() => {
    const fetchDetails = async () => {
      const response = await axios.get('http://localhost:8080/api/v1/user/my-details', {
        headers: {
          Authorization: 'Bearer ' + token,
        },
      });
      setUserDetails(response?.data);

      // Generate a random avatar URL
      const avatarNumber = Math.floor(Math.random() * 10) + 1;
      setAvatarUrl(`https://api.dicebear.com/7.x/fun-emoji/svg`);
    };

    fetchDetails();
  }, [token]);

  return (
    <div className="bg-gray-100 min-h-screen flex items-center justify-center font-ubuntu">
      <div className="bg-white p-8 rounded shadow-md w-80 md:w-96 lg:w-2/5 xl:w-1/3 text-left">
        {userDetails && (
          <div>
            <div className="mb-4">
              <img
                src={avatarUrl}
                alt="Avatar"
                className="mx-auto mb-4 h-20 w-20 rounded-full"
              />
              <div className="space-y-2">
                <DetailItem label="First Name" value={userDetails.firstName} />
                <DetailItem label="Last Name" value={userDetails.lastName} />
                <DetailItem label="Email" value={userDetails.email} />
              </div>
            </div>
            <div className="border-t border-gray-300 pt-4">
              <div className="space-y-2">
                <DetailItem label="Role" value={userDetails.role} />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

// Component for rendering a detail item with label and value
const DetailItem = ({ label, value }) => (
  <div className="flex justify-between">
    <p className="text-gray-600 font-semibold">{label}</p>
    <p>{value}</p>
  </div>
);

export default Dashboard;
