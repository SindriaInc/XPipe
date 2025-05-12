(function ($) {
  	"use strict";

  	$(document).on('click', '.box > h3, .box > h4', function(){
		$(this).toggleClass('active');
		$(this).next( ".hide" ).toggleClass('show');
	});

	jQuery(document).ready(function($){
    
	    //menu sortable
		var sortables = sortable('.admin-menus' ,{
			items: '.admin-menu-item',
			handle: 'h4',
			placeholderClass: 'box box-placeholder'
		});

		[].forEach.call(sortables, function(sortable) {
	        sortable.addEventListener('sortupdate', function(e) {
			    $('.admin-menu-item').each(function(){
					var item = $(this).find('input[data-index]');
					item.val( $(this).index() );
					item.attr('data-sort', $(this).index())
				});
			});
	    });

	    function updateSortable(){
	    	sortable('.admin-menus');
	    }

	    // add separator
		$(document).on('click', '.btn-add-separator', function(e){
			var $item = $('.admin-menu-separator:last'),
			$index = $item.siblings('.admin-menu-item').length + 1,
			$slug = $item.attr('data-menu-slug'),
			$str = $item[0].outerHTML.replaceAll($slug, 'separator_'+$index),
			$new = $($str);
			$new.find('[data-index]').val($index);
			$new.insertBefore($(this).prev());
			updateSortable();
		});

		// add menu
		$(document).on('click', '.btn-add-menu-atom, .btn-add-submenu-atom', function(e){
			var $item = $(this).siblings('.admin-menu-item:not(.admin-menu-separator):last'),
			$index = $item.siblings('.admin-menu-item').length + 1,
			$slug = $item.attr('data-menu-slug'),
			$custom_slug = $item.attr('data-default-slug')+'_'+$index,
			$custom_title = $item.attr('data-default-title'),
			$icon = 'dashicons-menu',
			$str = $item[0].outerHTML.replaceAll($slug, $custom_slug),
			$new = $($str);

			$new.find('input[type="text"]').val('');
			$new.find('[disabled]').removeAttr('disabled');
			$new.find('.admin-submenus-list').remove();
			$new.find('.admin-menu-title').html($custom_title);
			$new.find('[data-title]').val($custom_title);
			$new.find('[data-index]').val($index);
			$new.find('h4 i').attr('class', $icon);
			$new.find('h4 i').next().val($icon);
			$new.insertBefore($(this));
			$new.find('[data-url]').on('change', function(){
				var $url = encodeURI($(this).val());
				var $_url = $new.attr('data-menu-slug');
				$new.find('[name]').each(function(){
				  	var name = $(this).attr('name');
				  	$(this).attr('name', name.replace($_url, $url));
				});
				$new.attr('data-menu-slug', $url);
			});
			$new.find('[data-url]').val($custom_slug);

			updateSortable();
		});

		// icons switch
		$(document).on('click', '#tab-iconlist ul a', function(e){
			e.stopPropagation();
			e.preventDefault();
			var c = $('#tab-iconlist');
			c.find('.iconlist').hide();
			c.find('a.current').removeClass('current');
			$(this).addClass('current');
			$( $(this).attr('href') ).show();
		});

		// icons dropdown
		var select_icon;
		$('#dropdown').on('show.bs.dropdown', function (e) {
		  var  t = $('#dropdown')
		  	  ,i = $(e.relatedTarget)
		  	  ,p = i.parent().parent().position()
		  	  ;
		  $('div', '.iconlist').each(function(){
		  	$(this).removeClass('active');
		  	if($(this).hasClass( i.attr('class') )){
		  		$(this).addClass('active');
		  	}
		  });
		  select_icon = i;
		  t.css('top', p.top+42);
		})

		// select icon
		$(document).on('click', '.iconlist div', function(e){
			var c = $(this).attr('class');
			select_icon.attr('class', c);
			select_icon.next().val(c);
		});

		// color
		$('.color-field').wpColorPicker();

		// uploader
		$(document).on('click', '.upload-btn', function(e){
	        e.preventDefault();
	        var that = $(this);
	        var image = wp.media({ 
	            title: 'Upload Image',
	            multiple: false
	        }).open()
	        .on('select', function(e){
	            var uploaded_image = image.state().get('selection').first();
	            var image_url = uploaded_image.toJSON().url;
	            that.prev().val(image_url);
	        });
	    });
	});

})(jQuery);
