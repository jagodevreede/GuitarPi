import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-plectrum-config',
  templateUrl: './plectrum-config.component.html',
  styleUrls: ['./plectrum-config.component.css']
})
@Injectable()
export class PlectrumConfigComponent implements OnInit {
  private baseUrl = environment.baseUrl;
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

  hit(stringNumber: number) {
    this.http.post(this.baseUrl + 'config/plectrum', this.config).subscribe(res => {
      this.http.get(this.baseUrl + 'test/hit?string=' + stringNumber).subscribe(res => console.log("Hit string"));
    });
  }

}
