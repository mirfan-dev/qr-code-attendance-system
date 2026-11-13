import { publicAxios } from "./axios.service";


// register new user
export const registerUser = (userData) => {
   return publicAxios.post(`/users/register`, userData)
  .then((response) => response.data);
}