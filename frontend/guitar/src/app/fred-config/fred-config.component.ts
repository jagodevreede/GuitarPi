import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-fred-config',
  templateUrl: './fred-config.component.html',
  styleUrls: ['./fred-config.component.css']
})
export class FredConfigComponent implements OnInit {
  private baseUrl = 'http://localhost:8080/api/';
  //private baseUrl = 'http://192.168.1.20:8080/api/';
  public stringNumbers: Number[] = [];
  public fredNumbers: Number[] = [];
  public config: any[][];

  constructor(private http: HttpClient) {

  }

  ngOnInit() {
    this.http.get(this.baseUrl + 'config/fred')
          .subscribe(data => {
            this.config = <any[][]>data
            this.stringNumbers = Array(this.config.length)
                                      .fill(0)
                                      .map((x,i)=>i);
            this.fredNumbers = Array(this.config[0].length)
                                      .fill(0)
                                      .map((x,i)=>i);
          });
  }

  save() {
    this.http.post(this.baseUrl + 'config/fred', this.config).subscribe(res => console.log("Saved"));
  }

  test() {
    this.http.post(this.baseUrl + 'config/fred', this.config).subscribe(res => {
      this.http.post(this.baseUrl + 'test_fred', this.config).subscribe(res => console.log("Tested"));
    });
  }

}
