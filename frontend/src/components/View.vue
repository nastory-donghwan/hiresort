<template>

<v-data-table
    :headers="headers"
    :items="view"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'View',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "reservationid", value: "reservationid" },
            { text: "region", value: "region" },
            { text: "resort", value: "resort" },
            { text: "persons", value: "persons" },
            { text: "rooms", value: "rooms" },
            { text: "issuestatus", value: "issuestatus" },
            { text: "ticketnumber", value: "ticketnumber" },
            { text: "reservationstatus", value: "reservationstatus" },
        ],
        view : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/views')

      this.view = temp.data._embedded.views;

    },
    methods: {
    }
  }
</script>

