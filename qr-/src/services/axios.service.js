import { BASE_URL } from "./helper.service"
import axios from 'axios';


export const publicAxios = axios.create({
  baseURL: BASE_URL
})

export const privateAxios = axios.create({
  baseURL: BASE_URL
})