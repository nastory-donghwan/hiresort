<template>

<v-card style="width:300px; margin-left:5%;" outlined>
    <template slot="progress">
      <v-progress-linear
        color="deep-purple"
        height="10"
        indeterminate
      ></v-progress-linear>
    </template>

    <v-img
      style="width:290px; height:150px; border-radius:10px; position:relative; margin-left:5px; top:5px;"
      src="https://cdn.vuetifyjs.com/images/cards/cooking.png"
    ></v-img>

    <v-card-title v-if="value._links">
        Issued # {{value._links.self.href.split("/")[value._links.self.href.split("/").length - 1]}}
    </v-card-title >
    <v-card-title v-else>
        Issued
    </v-card-title >

    <v-card-text style = "margin-left:-15px; margin-top:10px;">

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="Reservationid" v-model="value.reservationid"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Reservationid :  {{value.reservationid }}
          </div>
          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Region" v-model="value.region"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Region :  {{value.region }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Resort" v-model="value.resort"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Resort :  {{value.resort }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="Persons" v-model="value.persons"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Persons :  {{value.persons }}
          </div>
          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="Rooms" v-model="value.rooms"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Rooms :  {{value.rooms }}
          </div>
          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Phone" v-model="value.phone"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Phone :  {{value.phone }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Email" v-model="value.email"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Email :  {{value.email }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Issuestatus" v-model="value.issuestatus"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Issuestatus :  {{value.issuestatus }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="Ticketnumber" v-model="value.ticketnumber"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Ticketnumber :  {{value.ticketnumber }}
          </div>

    </v-card-text>

    <v-divider class="mx-4"></v-divider>

    <v-card-actions style = "position:absolute; right:0; bottom:0;">
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="edit"
        v-if="!editMode"
      >
        Edit
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="save"
        v-else
      >
        Save
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="remove"
        v-if="!editMode"
      >
        Delete
      </v-btn>
    </v-card-actions>
  </v-card>


</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Issued',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        date: new Date().toISOString().substr(0, 10),
    }),

    methods: {
      edit(){
        this.editMode = true;
      },
      async save(){
        try{
          var temp = null;

          if(this.isNew){
            temp = await axios.post(axios.fixUrl('/issueds'), this.value)
          }else{
            temp = await axios.put(axios.fixUrl(this.value._links.self.href), this.value)
          }

          this.value = temp.data;

          this.editMode = false;
          this.$emit('input', this.value);

          if(this.isNew){
            this.$emit('add', this.value);
          }else{
            this.$emit('edit', this.value);
          }

        }catch(e){
          alert(e.message)
        }
      },
      async remove(){
        try{
          await axios.delete(axios.fixUrl(this.value._links.self.href))
          this.editMode = false;
          this.isDeleted = true;

          this.$emit('input', this.value);
          this.$emit('delete', this.value);

        }catch(e){
          alert(e.message)
        }
      },

    }
  }
</script>

