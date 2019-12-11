import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';

import { ToastrService } from 'ngx-toastr';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

	constructor(private toastrService: ToastrService) { }

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request)
			.pipe(
				retry(1),
				catchError((errorResponse: HttpErrorResponse) => {
					const errorMessage = errorResponse.error;
					this.toastrService.error(errorMessage, 'Error');
					return throwError(errorMessage);
				})
			);
	}
}
