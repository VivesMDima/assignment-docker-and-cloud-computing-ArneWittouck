import axios from 'axios';

export const api = axios.create({
  baseURL: 'https://orkest-hub-9089f910375e.herokuapp.com',
  timeout: 10000
});

