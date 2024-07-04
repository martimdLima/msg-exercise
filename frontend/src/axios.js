import axios from "axios";
const BASE_API_URL = "http://localhost:8080/api/mortality";

class MortalityRateService {
  retrieveAvailableYears() {
    //console.log("test")
    //console.log(`${BASE_API_URL}available/years`)
    return axios.get(`${BASE_API_URL}/available/years`);
  }

  retrieveDataByYear(year) {
    //console.log("test")
    //console.log(`${BASE_API_URL}available/years`)
    return axios.get(`${BASE_API_URL}/search/year/` + year);
  }
}

export default new MortalityRateService();