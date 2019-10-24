import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-fred-config',
  templateUrl: './fred-config.component.html',
  styleUrls: ['./fred-config.component.css']
})
export class FredConfigComponent implements OnInit {
  private baseUrl = environment.baseUrl;
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
    this.http.post(this.baseUrl + 'config/fred', this.config).subscribe(res => console.log("Saved Fred"));
  }

  test(stringNumber: Number, fredNumber: Number, pos) {
    this.http.post(this.baseUrl + 'config/fred', this.config).subscribe(res => {
      this.http.get(this.baseUrl + 'test/fred?string=' + stringNumber + '&fred=' + fredNumber + '&pos=' + pos).subscribe(res => console.log("Tested Fred"));
    });
  }

  resetAll() {
    this.http.post(this.baseUrl + 'config/fred', this.config).subscribe(res => {
      this.http.get(this.baseUrl + 'test/fred/reset_all').subscribe(res => console.log("Reset all Freds"));
    });
  }

  hit(stringNumber: number) {
      this.http.get(this.baseUrl + 'test/hit?string=' + stringNumber).subscribe(res => console.log("Hit string"));
  }

  reset(stringNumber: number) {
      this.http.get(this.baseUrl + 'test/reset?string=' + stringNumber).subscribe(res => console.log("reset string"));
  }

  testRow(stringNumber: number) {
      this.http.get(this.baseUrl + 'test/test?string=' + stringNumber).subscribe(res => console.log("test string"));
  }
}
