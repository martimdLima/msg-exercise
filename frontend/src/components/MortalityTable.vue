<template>
    <div id="app">
      <h1>Mortality Table</h1>
      <div>
        <label for="year">Select Year:</label>
        <select v-model="selectedYear" @change="fetchMortalityTable">
          <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
        </select>
      </div>
      <table v-if="mortalityTable.length">
        <thead>
          <tr>
            <th>Country</th>
            <th>Male Rate</th>
            <th>Male Popultion</th>
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
                <button v-if="!record.isEditing" @click="editRecord(index)">Edit</button>
                <div v-else>
                    <button @click="confirmEdit(index)">Confirm</button>
                    <button @click="cancelEdit(index)">Cancel</button>
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
    margin-top: 60px;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
  }
  
  table,
  th,
  td {
    border: 1px solid black;
  }
  
  th,
  td {
    padding: 10px;
    text-align: left;
  }
  </style>