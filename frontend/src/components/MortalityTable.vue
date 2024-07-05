<template>
  <div id="app">
    <h1>Mortality Table Viewer</h1>
    <div class="filter-section">
      <label for="year">Select Year:</label>
      <select v-model="selectedYear" @change="fetchMortalityTable">
        <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
      </select>
    </div>
    <table v-if="mortalityTable.length" class="mortality-table">
      <thead>
        <tr>
          <th>Country</th>
          <th>Male Rate</th>
          <th>Male Population</th>
          <th>Female Rate</th>
          <th>Female Population</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(record, index) in mortalityTable" :key="record.country">
          <td>{{ record.country }}</td>
          <td v-if="!record.isEditing">{{ record.maleRate }}</td>
          <td v-else><input v-model="record.maleRate" type="number" step="0.01" min="0" max="1000"></td>
          <td v-if="!record.isEditing">{{ record.malePopulation }}</td>
          <td v-else><input v-model="record.malePopulation" type="number" step="1" min="0" max="9999999"></td>
          <td v-if="!record.isEditing">{{ record.femaleRate }}</td>
          <td v-else><input v-model="record.femaleRate" type="number" step="0.01" min="0" max="9999999"></td>
          <td v-if="!record.isEditing">{{ record.femalePopulation }}</td>
          <td v-else><input v-model="record.femalePopulation" type="number" step="1" min="0" max="9999999"></td>
          <td>
            <button v-if="!record.isEditing" @click="editRecord(index)" class="btn btn-edit">Edit</button>
            <div v-else>
              <button @click="confirmEdit(index)" class="btn btn-confirm">Confirm</button>
              <button @click="cancelEdit(index)" class="btn btn-cancel">Cancel</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
  
  <script>
  import axios from 'axios';
  //import MortalityRateService from '../axios'
  
  export default {
    name: 'App',
    data() {
      return {
        selectedYear: 2023,
        years: [], // List of years to select from
        mortalityTable: []
      };
    },
    mounted() {
      this.loadAvailableYears()
      this.fetchMortalityTable();
    },
    methods: {
      loadAvailableYears() {
            axios.get(`http://localhost:8080/api/mortality/available/years`)
                .then(response => {
                    this.years = response.data.years;
                    this.selectedYear = response.data.years[this.years.length-1]
                })
            .catch(error => {
                console.error('Error fetching years:', error);
          });
      },
      fetchMortalityTable() {
        axios.get(`http://localhost:8080/api/mortality/search/year/${this.selectedYear}`)
          .then(response => {
            this.mortalityTable = response.data.map(record => ({
              ...record,
              isEditing: false
            }));
          })
          .catch(error => {
            console.error('Error fetching mortality table:', error);
          });
      },
      editRecord(index) {
        this.mortalityTable[index].isEditing = true;
      },
      confirmEdit(index) {
        const record = this.mortalityTable[index];
        axios.patch(`http://localhost:8080/api/mortality/update`, {
            id: record.id,
            year: record.year,
            country: record.country,
            maleRate: record.maleRate,
            malePopulation: record.maleRate,
            femaleRate: record.femaleRate,
            femalePopulation: record.femaleRate
        })
          .then(() => {
            this.mortalityTable[index].isEditing = false;
          })
          .catch(error => {
            console.error('Error updating record:', error);
          });
      },
      cancelEdit(index) {
        this.mortalityTable[index].isEditing = false;
        this.fetchMortalityTable(); // Re-fetch the table to discard changes
      }
    }
  };
  </script>
  
  <style>
  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    text-align: center;
    margin-top: 10px;
    padding: 20px;
    background-color: #f9f9f9;
  }
  
  h1 {
    color: #2c3e50;
    margin-bottom: 20px;
  }
  
  .filter-section {
    margin-bottom: 20px;
  }
  
  label {
    font-weight: bold;
    margin-right: 10px;
  }
  
  select {
    padding: 5px;
    border-radius: 4px;
    border: 1px solid #ccc;
  }
  
  .mortality-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    background-color: #fff;
  }
  
  .mortality-table th,
  .mortality-table td {
    padding: 25x;
    border: 1px solid #ddd;
  }
  
  .mortality-table th {
    background-color: #f2f2f2;
  }
  
  .btn {
    padding: 5px 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .btn-edit {
    background-color: #3498db;
    color: white;
  }
  
  .btn-confirm {
    background-color: #2ecc71;
    color: white;
  }
  
  .btn-cancel {
    background-color: #e74c3c;
    color: white;
    margin-left: 5px;
  }
  </style>