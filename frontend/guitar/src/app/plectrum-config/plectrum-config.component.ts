import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-plectrum-config',
  templateUrl: './plectrum-config.component.html',
  styleUrls: ['./plectrum-config.component.css']
})
@Injectable()
export class PlectrumConfigComponent implements OnInit {
  //private baseUrl = 'http://localhost:8080/api/';
  private baseUrl = 'http://192.168.1.20:8080/api/';
  public config: any;
  public heights = [ "Free", "Soft", "Hard", "Stop" ]
  public positions = [ "Up", "Down" ]

  constructor(private http: HttpClient) {

  }

  ngOnInit() {
    this.http.get(this.baseUrl + 'config/plectrum')
          .subscribe(data => {
            this.config = data
            for (let c of this.config) {
                c.height = "Free"
                c.position = "Up"
            }
          });
  }

  save() {
    this.http.post(this.baseUrl + 'config/plectrum', this.config).subscribe(res => console.log("Saved"));
  }

  test() {
    this.http.post(this.baseUrl + 'config/plectrum', this.config).subscribe(res => {
      this.http.post(this.baseUrl + 'test', this.config).subscribe(res => console.log("Tested"));
    });
  }

}
